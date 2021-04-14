package com.unascribed.yttr.block.mechanism;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class FlopperBlock extends HopperBlock {

	private static final ImmutableMap<Direction, VoxelShape> SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
			.put(Direction.NORTH, Stream.of(
					createCuboidShape(0, 0, 5, 16, 16, 6),
					createCuboidShape(14, 0, 0, 16, 16, 5),
					createCuboidShape(0, 0, 0, 2, 16, 5),
					createCuboidShape(2, 0, 0, 14, 2, 5),
					createCuboidShape(2, 14, 0, 14, 16, 5),
					createCuboidShape(4, 4, 6, 12, 12, 12),
					createCuboidShape(6, 0, 8, 10, 4, 12)
				).reduce((a, b) -> VoxelShapes.combineAndSimplify(a, b, BooleanBiFunction.OR)).get())
			.put(Direction.EAST, Stream.of(
					createCuboidShape(10, 0, 0, 11, 16, 16),
					createCuboidShape(11, 0, 14, 16, 16, 16),
					createCuboidShape(11, 0, 0, 16, 16, 2),
					createCuboidShape(11, 0, 2, 16, 2, 14),
					createCuboidShape(11, 14, 2, 16, 16, 14),
					createCuboidShape(4, 4, 4, 10, 12, 12),
					createCuboidShape(4, 0, 6, 8, 4, 10)
				).reduce((a, b) -> VoxelShapes.combineAndSimplify(a, b, BooleanBiFunction.OR)).get())
			.put(Direction.SOUTH, Stream.of(
					createCuboidShape(0, 0, 10, 16, 16, 11),
					createCuboidShape(0, 0, 11, 2, 16, 16),
					createCuboidShape(14, 0, 11, 16, 16, 16),
					createCuboidShape(2, 0, 11, 14, 2, 16),
					createCuboidShape(2, 14, 11, 14, 16, 16),
					createCuboidShape(4, 4, 4, 12, 12, 10),
					createCuboidShape(6, 0, 4, 10, 4, 8)
				).reduce((a, b) -> VoxelShapes.combineAndSimplify(a, b, BooleanBiFunction.OR)).get())
			.put(Direction.WEST, Stream.of(
					createCuboidShape(5, 0, 0, 6, 16, 16),
					createCuboidShape(0, 0, 0, 5, 16, 2),
					createCuboidShape(0, 0, 14, 5, 16, 16),
					createCuboidShape(0, 0, 2, 5, 2, 14),
					createCuboidShape(0, 14, 2, 5, 16, 14),
					createCuboidShape(6, 4, 4, 12, 12, 12),
					createCuboidShape(8, 0, 6, 12, 4, 10)
				).reduce((a, b) -> VoxelShapes.combineAndSimplify(a, b, BooleanBiFunction.OR)).get())
			.build();
	
	private static final ImmutableMap<Direction, VoxelShape> FUNNEL_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
			.put(Direction.NORTH, createCuboidShape(2, 2, 0, 14, 14, 5))
			.put(Direction.EAST, createCuboidShape(11, 2, 2, 16, 14, 14))
			.put(Direction.SOUTH, createCuboidShape(2, 2, 11, 14, 14, 16))
			.put(Direction.WEST, createCuboidShape(0, 2, 2, 5, 14, 14))
			.build();
	
	// Bring me Ray Shapes!
	private static final ImmutableMap<Direction, VoxelShape> RAY_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
			.put(Direction.NORTH, VoxelShapes.combineAndSimplify(FUNNEL_SHAPES.get(Direction.NORTH), createCuboidShape(6, 0, 6, 10, 4, 8), BooleanBiFunction.OR))
			.put(Direction.EAST, VoxelShapes.combineAndSimplify(FUNNEL_SHAPES.get(Direction.EAST), createCuboidShape(8, 0, 6, 10, 4, 10), BooleanBiFunction.OR))
			.put(Direction.SOUTH, VoxelShapes.combineAndSimplify(FUNNEL_SHAPES.get(Direction.SOUTH), createCuboidShape(6, 0, 8, 10, 4, 10), BooleanBiFunction.OR))
			.put(Direction.WEST, VoxelShapes.combineAndSimplify(FUNNEL_SHAPES.get(Direction.WEST), createCuboidShape(6, 0, 6, 8, 4, 10), BooleanBiFunction.OR))
			.build();
	
	public FlopperBlock(Settings settings) {
		super(settings);
	}
	
	public VoxelShape getFunnelShape(BlockState state) {
		return FUNNEL_SHAPES.getOrDefault(state.get(FACING), Hopper.INPUT_AREA_SHAPE);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES.containsKey(state.get(FACING)) ? SHAPES.get(state.get(FACING)) : super.getOutlineShape(state, world, pos, context);
	}
	
	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return RAY_SHAPES.containsKey(state.get(FACING)) ? RAY_SHAPES.get(state.get(FACING)) : super.getRaycastShape(state, world, pos);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? ctx.getPlayerFacing().getOpposite() : direction).with(ENABLED, true);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new FlopperBlockEntity();
	}

}
