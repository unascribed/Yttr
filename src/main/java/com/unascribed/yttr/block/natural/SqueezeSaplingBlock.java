package com.unascribed.yttr.block.natural;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SqueezeSaplingBlock extends SaplingBlock implements Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public SqueezeSaplingBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.GRAVEL) || floor.isOf(Blocks.SAND);
	}
	
	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return state.get(WATERLOGGED) && super.canGrow(world, random, pos, state);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
		if (fluid.isIn(FluidTags.WATER)) state = state.with(WATERLOGGED, true);
		return state;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
