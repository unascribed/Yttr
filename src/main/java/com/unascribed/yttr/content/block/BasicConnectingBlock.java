package com.unascribed.yttr.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BasicConnectingBlock extends ConnectingBlock {

	public BasicConnectingBlock(float radius, Settings settings) {
		super(radius, settings);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(DOWN, UP, NORTH, SOUTH, EAST, WEST);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
	}
	
	public boolean connectsTo(BlockState bs) {
		return bs.isOf(this);
	}

	public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
		BlockState bs = getDefaultState();
		for (Direction d : DIRECTIONS) {
			bs = bs.with(FACING_PROPERTIES.get(d), connectsTo(world.getBlockState(pos.offset(d))));
		}
		return bs;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return state.with(FACING_PROPERTIES.get(direction), connectsTo(neighborState));
	}
	
}
