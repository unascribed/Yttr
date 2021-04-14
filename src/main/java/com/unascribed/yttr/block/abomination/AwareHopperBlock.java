package com.unascribed.yttr.block.abomination;

import java.util.List;
import java.util.stream.Stream;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
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

	private static final VoxelShape BASE_SHAPE = Stream.of(
			createCuboidShape(0, 8, 0, 2, 16, 16),
			createCuboidShape(2, 8, 0, 14, 13, 2),
			createCuboidShape(14, 8, 0, 16, 16, 16),
			createCuboidShape(2, 8, 14, 14, 13, 16),
			createCuboidShape(3, 8, 3, 13, 10, 13),
			createCuboidShape(4, 4, 4, 12, 7, 12),
			createCuboidShape(0, 7, 0, 16, 8, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
	
	private static final VoxelShape HEAD_SHAPE = createCuboidShape(4, 20, 4, 12, 32, 12);
	private static final VoxelShape PUMPKIN_SHAPE = createCuboidShape(3, 22, 3, 13, 32, 13);

	private static final ImmutableMap<Direction, VoxelShape> BASE_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
			.put(Direction.DOWN,  VoxelShapes.union(BASE_SHAPE, createCuboidShape(6, 0, 6, 10, 4, 10)))
			.put(Direction.EAST,  VoxelShapes.union(BASE_SHAPE, createCuboidShape(12, 4, 6, 16, 8, 10)))
			.put(Direction.NORTH,  VoxelShapes.union(BASE_SHAPE, createCuboidShape(6, 4, 0, 10, 8, 4)))
			.put(Direction.SOUTH,  VoxelShapes.union(BASE_SHAPE, createCuboidShape(6, 4, 12, 10, 8, 16)))
			.put(Direction.WEST, VoxelShapes.union(BASE_SHAPE, createCuboidShape(0, 4, 6, 4, 8, 10)))
			.build();
	
	private static final ImmutableMap<Direction, VoxelShape> NORMAL_SHAPES = ImmutableMap.copyOf(Maps.transformValues(BASE_SHAPES, s -> VoxelShapes.union(s, HEAD_SHAPE)));
	private static final ImmutableMap<Direction, VoxelShape> BLIND_SHAPES = ImmutableMap.copyOf(Maps.transformValues(BASE_SHAPES, s -> VoxelShapes.union(s, PUMPKIN_SHAPE)));

	private static final VoxelShape DOWN_RAY_TRACE_SHAPE = VoxelShapes.union(BASE_SHAPE, createCuboidShape(2, 9, 2, 14, 16, 14));
	private static final VoxelShape EAST_RAY_TRACE_SHAPE = VoxelShapes.union(DOWN_RAY_TRACE_SHAPE, createCuboidShape(12, 8, 6, 16, 10, 10));
	private static final VoxelShape NORTH_RAY_TRACE_SHAPE = VoxelShapes.union(DOWN_RAY_TRACE_SHAPE, createCuboidShape(6, 8, 0, 10, 10, 4));
	private static final VoxelShape SOUTH_RAY_TRACE_SHAPE = VoxelShapes.union(DOWN_RAY_TRACE_SHAPE, createCuboidShape(6, 8, 12, 10, 10, 16));
	private static final VoxelShape WEST_RAY_TRACE_SHAPE = VoxelShapes.union(DOWN_RAY_TRACE_SHAPE, createCuboidShape(0, 8, 6, 4, 10, 10));

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
		if (state.get(BLIND)) {
			return BLIND_SHAPES.getOrDefault(state.get(FACING), BASE_SHAPE);
		} else {
			return NORMAL_SHAPES.getOrDefault(state.get(FACING), BASE_SHAPE);
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
		world.playSound(null, pos, YSounds.AWARE_HOPPER_BREAK, SoundCategory.BLOCKS, 1, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
	}


}
