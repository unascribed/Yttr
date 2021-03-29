package com.unascribed.yttr;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AwareHopperBlock extends Block implements BlockEntityProvider {

	public static final DirectionProperty FACING = Properties.HOPPER_FACING;
	public static final BooleanProperty BLIND = BooleanProperty.of("blind");

	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0D, 7.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 7.0D, 12.0D);
	private static final VoxelShape OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, Hopper.INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);

	private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
	private static final VoxelShape EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
	private static final VoxelShape WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));

	private static final VoxelShape DOWN_RAY_TRACE_SHAPE = Hopper.INSIDE_SHAPE;
	private static final VoxelShape EAST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
	private static final VoxelShape NORTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
	private static final VoxelShape SOUTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
	private static final VoxelShape WEST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

	public AwareHopperBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(BLIND, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(BLIND);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (state.get(BLIND)) {
			if (player.abilities.creativeMode) {
				// always succeed
			} else if (stack.isEmpty()) {
				player.setStackInHand(hand, new ItemStack(Blocks.CARVED_PUMPKIN));
			} else if (stack.getItem() == Blocks.CARVED_PUMPKIN.asItem() && stack.getCount() < stack.getMaxCount()) {
				stack.increment(1);
				player.setStackInHand(hand, stack);
			} else {
				return ActionResult.PASS;
			}
			world.setBlockState(pos, state.with(BLIND, false));
			return ActionResult.SUCCESS;
		} else {
			if (stack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
				if (!player.abilities.creativeMode) {
					stack.decrement(1);
					player.setStackInHand(hand, stack);
				}
				world.setBlockState(pos, state.with(BLIND, true));
				return ActionResult.SUCCESS;
			} else {
				// debug. remove later
				player.openHandledScreen(new NamedScreenHandlerFactory() {
					
					@Override
					public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
						return GenericContainerScreenHandler.createGeneric9x3(syncId, inv, (AwareHopperBlockEntity)world.getBlockEntity(pos));
					}
					
					@Override
					public Text getDisplayName() {
						return new TranslatableText("block.yttr.aware_hopper");
					}
				});
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new AwareHopperBlockEntity();
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be != null) {
			be.onSyncedBlockEvent(type, data);
		}
		return true;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch(state.get(FACING)) {
			case DOWN:
				return DOWN_SHAPE;
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			case EAST:
				return EAST_SHAPE;
			default:
				return DEFAULT_SHAPE;
		}
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		switch(state.get(FACING)) {
			case DOWN:
				return DOWN_RAY_TRACE_SHAPE;
			case NORTH:
				return NORTH_RAY_TRACE_SHAPE;
			case SOUTH:
				return SOUTH_RAY_TRACE_SHAPE;
			case WEST:
				return WEST_RAY_TRACE_SHAPE;
			case EAST:
				return EAST_RAY_TRACE_SHAPE;
			default:
				return Hopper.INSIDE_SHAPE;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction);
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		List<ItemStack> li = super.getDroppedStacks(state, builder);
		if (state.get(BLIND)) {
			li = Lists.newArrayList(li);
			li.add(new ItemStack(Blocks.CARVED_PUMPKIN));
		}
		return li;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof AwareHopperBlockEntity) {
				((AwareHopperBlockEntity) be).drop();
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}
	
	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
		world.playSound(null, pos, Yttr.AWARE_HOPPER_BREAK, SoundCategory.BLOCKS, 1, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
	}


}
