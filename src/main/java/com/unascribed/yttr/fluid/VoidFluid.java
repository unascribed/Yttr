package com.unascribed.yttr.fluid;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class VoidFluid extends FlowableFluid {

	public static class Flowing extends VoidFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}
	
	public static class Still extends VoidFluid {
		
		@Override
		public int getLevel(FluidState state) {
			return 8;
		}
		
		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
		
	}
	
	@Override
	public Fluid getFlowing() {
		return YFluids.FLOWING_VOID;
	}

	@Override
	public Fluid getStill() {
		return YFluids.VOID;
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return 1;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}

	@Override
	public Item getBucketItem() {
		return YItems.VOID_BUCKET;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 1;
	}

	@Override
	protected float getBlastResistance() {
		return 100;
	}
	
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid instanceof VoidFluid;
	}
	
	@Override
	public float getHeight(FluidState state) {
		return (super.getHeight(state)*2)/3;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return YBlocks.VOID.getDefaultState().with(FluidBlock.LEVEL, method_15741(state));
	}

}
