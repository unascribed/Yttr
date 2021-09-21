package com.unascribed.yttr.content.block;

import com.unascribed.yttr.init.YTags;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RuinedPipeBlock extends BasicConnectingBlock {

	public RuinedPipeBlock(Settings settings) {
		super(0.25f, settings);
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this);
	}
	
	@Override
	public boolean connectsTo(BlockState bs) {
		return bs.isOf(this) || bs.isIn(YTags.Block.RUINED_DEVICES);
	}
	
	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(super.getOutlineShape(state, world, pos, context).getBoundingBox());
	}

}
