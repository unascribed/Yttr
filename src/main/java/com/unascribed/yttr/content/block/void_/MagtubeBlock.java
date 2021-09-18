package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MagtubeBlock extends ConnectingBlock implements Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public MagtubeBlock(Settings settings) {
		super(0.375f, settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(DOWN, UP, NORTH, SOUTH, EAST, WEST, WATERLOGGED);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return withConnectionProperties(ctx.getWorld(), ctx.getBlockPos()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isIn(FluidTags.WATER));
	}
	
	public boolean connectsTo(BlockState bs) {
		return bs.isOf(this) || bs.isOf(YBlocks.MAGTANK) || bs.isOf(YBlocks.VOID_FILTER) || bs.isOf(YBlocks.DSU);
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
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
