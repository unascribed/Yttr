package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class InRedLogicTileBlock extends InRedDeviceBlock implements Waterloggable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty BOOLEAN_MODE = BooleanProperty.of("boolean_mode");

	public static final VoxelShape CLICK_BOOLEAN = Block.createCuboidShape( 6, 2.9,  3, 10, 4.1,  7);
	public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);

	public InRedLogicTileBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return InRedLogic.isSideSolid(world, pos.down(), Direction.UP)
				//TODO: are these conditions even necessary?
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_SCAFFOLD
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_BLOCK;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BASE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
