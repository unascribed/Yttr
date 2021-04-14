package com.unascribed.yttr.block.mechanism;

import java.util.List;
import java.util.Locale;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ChuteBlock extends Block implements BlockEntityProvider, Waterloggable {

	public enum Mode implements StringIdentifiable {
		DROP,
		PASS,
		TAKE,
		CLOGGED,
		LEVITATE,
		LEVITATE_DROP,
		LEVITATE_INSERT,
		LEVITATE_CLOGGED,
		;

		@Override
		public String asString() {
			return name().toLowerCase(Locale.ROOT);
		}
		
		public boolean isDroppy() {
			return this == DROP || this == PASS || this == TAKE;
		}
		
		public boolean isLevitatey() {
			return this == LEVITATE || this == LEVITATE_DROP || this == LEVITATE_INSERT;
		}
		
		public boolean isClogged() {
			return this == CLOGGED || this == LEVITATE_CLOGGED;
		}
		
	}
	
	public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);
	public static final BooleanProperty PLATED = BooleanProperty.of("plated");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public static final VoxelShape SHAPE = VoxelShapes.cuboid(4/16D, 0, 4/16D, 12/16D, 16/16D, 12/16D);
	public static final VoxelShape SHAPE_PLATED = VoxelShapes.union(SHAPE, VoxelShapes.cuboid(0, 15/16D, 0, 1, 1, 1));
	public static final VoxelShape SHAPE_PLATED_COLLISION = VoxelShapes.union(SHAPE, VoxelShapes.cuboid(0.005, 15/16D, 0.005, 0.995, 1, 0.995));
	
	public ChuteBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(PLATED, false).with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(MODE);
		builder.add(PLATED);
		builder.add(WATERLOGGED);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ChuteBlockEntity();
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(PLATED) ? SHAPE_PLATED : SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(PLATED) ? SHAPE_PLATED_COLLISION : SHAPE;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (!state.get(PLATED)) {
			if (stack.getItem() == Items.IRON_INGOT) {
				if (!player.abilities.creativeMode) {
					stack.decrement(1);
					player.setStackInHand(hand, stack);
				}
				world.setBlockState(pos, state.with(PLATED, true));
				player.playSound(YSounds.CHUTE_PLATED, 1, 1);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		List<ItemStack> li = super.getDroppedStacks(state, builder);
		if (state.get(PLATED)) {
			li = Lists.newArrayList(li);
			li.add(new ItemStack(Items.IRON_INGOT));
		}
		return li;
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (state.get(PLATED)) {
			Vec3d vel = entity.getVelocity();
			if (entity.horizontalCollision) {
				entity.setVelocity(vel.x, 0.25, vel.z);
			} else if (entity.isSneaking()) {
				entity.setVelocity(vel.x, 0.08, vel.z);
			} else if (vel.y < -0.2) {
				entity.setVelocity(vel.x, -0.2, vel.z);
			}
			entity.fallDistance = 0;
		}
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (direction == Direction.DOWN) {
			Mode mode = state.get(MODE);
			if (newState.isOf(this)) {
				Mode theirMode = newState.get(MODE);
				if (theirMode.isDroppy()) {
					mode = Mode.PASS;
				} else {
					mode = theirMode;
				}
			} else if (newState.isOf(YBlocks.LEVITATION_CHAMBER)) {
				mode = Mode.LEVITATE;
			} else if (newState.getCollisionShape(world, posFrom).isEmpty() || getStairRedirection(Direction.DOWN, newState) != null) {
				mode = Mode.DROP;
			} else if (world.getBlockEntity(posFrom) instanceof Inventory) {
				mode = Mode.PASS;
			} else {
				mode = Mode.CLOGGED;
			}
			state = state.with(MODE, mode);
			BlockPos up = pos.up();
			BlockState upState = world.getBlockState(up);
			if (mode.isLevitatey()) {
				state = getStateForNeighborUpdate(state, Direction.UP, upState, world, pos, up);
			} else if (mode.isDroppy()) {
				if (upState.getCollisionShape(world, posFrom).isEmpty()) {
					state = state.with(MODE, Mode.TAKE);
				}
			}
		} else if (direction == Direction.UP) {
			Mode mode = state.get(MODE);
			if (mode.isDroppy()) {
				if (newState.getCollisionShape(world, posFrom).isEmpty()) {
					mode = Mode.TAKE;
				} else if (mode == Mode.TAKE) {
					BlockPos down = pos.down();
					return getStateForNeighborUpdate(state, Direction.DOWN, world.getBlockState(down), world, pos, down);
				}
			} else if (mode.isLevitatey() || mode == Mode.LEVITATE_CLOGGED) {
				if (newState.isOf(this)) {
					Mode theirMode = newState.get(MODE);
					if (!theirMode.isClogged()) {
						mode = Mode.LEVITATE;
					} else {
						mode = theirMode;
					}
				} else if (newState.getCollisionShape(world, posFrom).isEmpty() || getStairRedirection(Direction.UP, newState) != null) {
					mode = Mode.LEVITATE_DROP;
				} else if (world.getBlockEntity(posFrom) instanceof Inventory) {
					mode = Mode.LEVITATE_INSERT;
				} else {
					mode = Mode.LEVITATE_CLOGGED;
				}
			}
			state = state.with(MODE, mode);
		}
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return state;
	}
	
	public static Direction getStairRedirection(Direction dir, BlockState state) {
		if (state.getBlock() instanceof StairsBlock && state.contains(StairsBlock.FACING) && state.contains(StairsBlock.HALF) && state.contains(StairsBlock.SHAPE)) {
			if (state.get(StairsBlock.SHAPE) != StairShape.STRAIGHT) return null;
			boolean topStair = state.get(StairsBlock.HALF) == BlockHalf.TOP;
			if ((dir == Direction.UP) == topStair) {
				return state.get(StairsBlock.FACING).getOpposite();
			}
		}
		return null;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = getDefaultState();
		BlockPos down = ctx.getBlockPos().down();
		state = getStateForNeighborUpdate(state, Direction.DOWN, ctx.getWorld().getBlockState(down), ctx.getWorld(), ctx.getBlockPos(), down);
		BlockPos up = ctx.getBlockPos().up();
		state = getStateForNeighborUpdate(state, Direction.UP, ctx.getWorld().getBlockState(up), ctx.getWorld(), ctx.getBlockPos(), up);
		FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
		if (fluid.isIn(FluidTags.WATER)) state = state.with(WATERLOGGED, true);
		return state;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
