package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.CableConnection;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InRedCableBlock extends Block implements Waterloggable {
	public static final EnumProperty<CableConnection> NORTH = EnumProperty.of("north", CableConnection.class);
	public static final EnumProperty<CableConnection> SOUTH = EnumProperty.of("south", CableConnection.class);
	public static final EnumProperty<CableConnection> EAST = EnumProperty.of("east", CableConnection.class);
	public static final EnumProperty<CableConnection> WEST = EnumProperty.of("west", CableConnection.class);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	private static final VoxelShape NO_SIDE = Block.createCuboidShape(6d, 0d, 6d, 10d, 3d, 10d);
	private static final VoxelShape NORTH_SIDE = Block.createCuboidShape(6d, 0d, 0d, 10d, 3d, 6d);
	private static final VoxelShape NORTH_UP = Block.createCuboidShape(6d, 3d, 0d, 10d, 19d, 3d);
	private static final VoxelShape NORTH_SIDE_UP = VoxelShapes.union(NORTH_SIDE, NORTH_UP);
	private static final VoxelShape SOUTH_SIDE = Block.createCuboidShape(6d, 0d, 10d, 10d, 3d, 16d);
	private static final VoxelShape SOUTH_UP = Block.createCuboidShape(6d, 3d, 13d, 10d, 19d, 16d);
	private static final VoxelShape SOUTH_SIDE_UP = VoxelShapes.union(SOUTH_SIDE, SOUTH_UP);
	private static final VoxelShape EAST_SIDE = Block.createCuboidShape(10d, 0d, 6d, 16d, 3d, 10d);
	private static final VoxelShape EAST_UP = Block.createCuboidShape(13d, 3d, 6d, 16d, 19d, 10d);
	private static final VoxelShape EAST_SIDE_UP = VoxelShapes.union(EAST_SIDE, EAST_UP);
	private static final VoxelShape WEST_SIDE = Block.createCuboidShape(0d, 0d, 6d, 6d, 3d, 10d);
	private static final VoxelShape WEST_UP = Block.createCuboidShape(0d, 3d, 6d, 3d, 19d, 10d);
	private static final VoxelShape WEST_SIDE_UP = VoxelShapes.union(WEST_SIDE, WEST_UP);

	public InRedCableBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(NORTH, CableConnection.DISCONNECTED)
				.with(SOUTH, CableConnection.DISCONNECTED)
				.with(EAST, CableConnection.DISCONNECTED)
				.with(WEST, CableConnection.DISCONNECTED)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		VoxelShape result = NO_SIDE;
		if (state.get(NORTH) == CableConnection.CONNECTED) result = VoxelShapes.union(result, NORTH_SIDE);
		if (state.get(NORTH) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, NORTH_SIDE_UP);
		if (state.get(SOUTH) == CableConnection.CONNECTED) result = VoxelShapes.union(result, SOUTH_SIDE);
		if (state.get(SOUTH) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, SOUTH_SIDE_UP);
		if (state.get(EAST) == CableConnection.CONNECTED) result = VoxelShapes.union(result, EAST_SIDE);
		if (state.get(EAST) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, EAST_SIDE_UP);
		if (state.get(WEST) == CableConnection.CONNECTED) result = VoxelShapes.union(result, WEST_SIDE);
		if (state.get(WEST) == CableConnection.CONNECTED_UP) result = VoxelShapes.union(result, WEST_SIDE_UP);
		return result;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	private CableConnection getCableConnections(BlockView world, BlockPos pos, Direction dir) {
		if (InRedLogic.canConnect(world, pos.offset(dir), dir.getOpposite())) return CableConnection.CONNECTED;

		if (!InRedLogic.isSideSolid((World)world, pos.offset(Direction.UP), Direction.DOWN)) {
			if (InRedLogic.canConnect(world, pos.offset(dir).up(), dir.getOpposite())) return CableConnection.CONNECTED_UP;
		}

		if (!InRedLogic.isSideSolid((World)world, pos.offset(dir), dir.getOpposite())) {
			if (world.getBlockState(pos.offset(Direction.DOWN).offset(dir)).getBlock() == YBlocks.INRED_SCAFFOLD
					|| world.getBlockState(pos.offset(Direction.DOWN).offset(dir)).getBlock() == YBlocks.INRED_BLOCK) return CableConnection.DISCONNECTED;
			if (InRedLogic.canConnect(world, pos.offset(dir).down(), dir.getOpposite())) return CableConnection.CONNECTED;
		}

		return CableConnection.DISCONNECTED;
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
				.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.up(), this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.up(), this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.up(), this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		world.setBlockState(pos, this.getDefaultState()
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER));

	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
