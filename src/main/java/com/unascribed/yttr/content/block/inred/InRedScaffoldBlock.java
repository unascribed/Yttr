package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.InRedProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InRedScaffoldBlock extends Block implements Waterloggable {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public InRedScaffoldBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(NORTH, false)
				.with(SOUTH, false)
				.with(EAST, false)
				.with(WEST, false)
				.with(UP, false)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, WATERLOGGED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Block.createCuboidShape(0.05,0.0,0.05,15.95,16.0,15.95);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof ItemEntity) return;
		if (entity.horizontalCollision) {
			entity.setVelocity(entity.getVelocity().x, 0.35, entity.getVelocity().z);
		} else if (entity.isSneaking()) {
			entity.setVelocity(entity.getVelocity().x, 0.08, entity.getVelocity().z); //Stop, but also counteract EntityLivingBase-applied microgravity
		} else if (entity.getVelocity().y<=0.20) {
			entity.setVelocity(entity.getVelocity().x, -0.20, entity.getVelocity().z);
		}
	}

	private boolean getCableConnections(BlockView world, BlockPos pos, Direction dir) {
		if (world.getBlockState(pos.offset(dir).offset(Direction.DOWN)).getBlock() == YBlocks.INRED_CABLE) return true;
		return InRedLogic.canConnect(world, pos.offset(dir), dir.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		return this.getDefaultState()
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(UP, getCableConnections(world, pos, Direction.UP))
				.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		world.setBlockState(pos, state
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(UP, getCableConnections(world, pos, Direction.UP))
				.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER)
		);
	}


	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		for (Direction dir : Direction.values()) {
			world.updateNeighborsAlways(pos.offset(dir), this);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		for (Direction dir : Direction.values()) {
			world.updateNeighborsAlways(pos.offset(dir), this);
		}
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
