package com.unascribed.yttr.block;

import com.unascribed.yttr.block.entity.DopperBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class DopperBlock extends HopperBlock {

	private final VoxelShape WESTEAST_SHAPE;
	private final VoxelShape NORTHSOUTH_SHAPE;
	private final VoxelShape WESTEAST_RAY_SHAPE;
	private final VoxelShape NORTHSOUTH_RAY_SHAPE;
	
	public DopperBlock(Settings settings) {
		super(settings);
		WESTEAST_SHAPE = VoxelShapes.combineAndSimplify(
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.WEST).getOutlineShape(null, null),
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.EAST).getOutlineShape(null, null),
				BooleanBiFunction.OR
			);
		NORTHSOUTH_SHAPE = VoxelShapes.combineAndSimplify(
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.NORTH).getOutlineShape(null, null),
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.SOUTH).getOutlineShape(null, null),
				BooleanBiFunction.OR
			);
		WESTEAST_RAY_SHAPE = VoxelShapes.combineAndSimplify(
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.WEST).getRaycastShape(null, null),
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.EAST).getRaycastShape(null, null),
				BooleanBiFunction.OR
			);
		NORTHSOUTH_RAY_SHAPE = VoxelShapes.combineAndSimplify(
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.NORTH).getRaycastShape(null, null),
				Blocks.HOPPER.getDefaultState().with(FACING, Direction.SOUTH).getRaycastShape(null, null),
				BooleanBiFunction.OR
			);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (state.get(FACING)) {
			case NORTH: case SOUTH:
				return NORTHSOUTH_SHAPE;
			case WEST: case EAST:
				return WESTEAST_SHAPE;
			default:
				return super.getOutlineShape(state, world, pos, context);
		}
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		switch (state.get(FACING)) {
			case NORTH: case SOUTH:
				return NORTHSOUTH_RAY_SHAPE;
			case WEST: case EAST:
				return WESTEAST_RAY_SHAPE;
			default:
				return super.getRaycastShape(state, world, pos);
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? ctx.getPlayerFacing() : direction).with(ENABLED, true);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DopperBlockEntity();
	}

}
