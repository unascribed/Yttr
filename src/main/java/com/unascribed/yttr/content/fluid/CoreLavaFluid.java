package com.unascribed.yttr.content.fluid;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CoreLavaFluid extends LavaFluid {

	@Override
	public boolean isStill(FluidState state) {
		return true;
	}

	@Override
	public int getLevel(FluidState state) {
		return 8;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}
	
	@Override
	public ParticleEffect getParticle() {
		return null;
	}

	@Override
	public Fluid getStill() {
		return YFluids.CORE_LAVA;
	}

	@Override
	public Item getBucketItem() {
		return Items.AIR;
	}
	
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == this;
	}
	
	@Override
	public int getFlowSpeed(WorldView world) {
		return 1000;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 1000;
	}
	
	@Override
	protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
	}
	
	@Override
	protected boolean isInfinite() {
		return true;
	}
	
	@Override
	public BlockState toBlockState(FluidState state) {
		return YBlocks.CORE_LAVA.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}
	
}
