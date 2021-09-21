package com.unascribed.yttr.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public class BasicFacingBlock extends FacingBlock {

	public BasicFacingBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction d = ctx.getPlayerLookDirection();
		return getDefaultState().with(FACING, d.getAxis() == Axis.Y ? d.getOpposite() : d);
	}
	
}
