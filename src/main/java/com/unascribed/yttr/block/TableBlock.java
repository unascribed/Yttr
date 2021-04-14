package com.unascribed.yttr.block;

import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class TableBlock extends Block {

	private static final VoxelShape SHAPE = Stream.of(
			createCuboidShape(0, 0, 0, 2, 14, 2),
			createCuboidShape(0, 0, 14, 2, 14, 16),
			createCuboidShape(14, 0, 0, 16, 14, 2),
			createCuboidShape(14, 0, 14, 16, 14, 16),
			createCuboidShape(0, 14, 0, 16, 16, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
	
	public TableBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

}
