package com.unascribed.yttr.content.fluid;

import java.util.Random;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;

import com.google.common.hash.Hashing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class PureVoidFluid extends VoidFluid {

	public static class Flowing extends PureVoidFluid {
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
	
	public static class Still extends PureVoidFluid {
		
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
		return YFluids.FLOWING_PURE_VOID;
	}

	@Override
	public Fluid getStill() {
		return YFluids.PURE_VOID;
	}

	@Override
	public Item getBucketItem() {
		return Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return matchesType(fluid);
	}
	
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid instanceof PureVoidFluid;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return YBlocks.PURE_VOID.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}
	
	@Override
	protected boolean hasRandomTicks() {
		return true;
	}
	
	@Override
	public int getTickRate(WorldView world) {
		return 1;
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
	public void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
		if (state.isStill() && random.nextInt(100) == 0 && world.getBlockState(pos).isOf(YBlocks.PURE_VOID)) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
		Direction dir = Direction.byId(random.nextInt(5)+1);
		BlockPos toDestroy = pos.offset(dir);
		BlockState desState = world.getBlockState(toDestroy);
		if (!desState.isOf(YBlocks.PURE_VOID) && !desState.isOf(YBlocks.VOID_FILTER) && !desState.isOf(YBlocks.ERODED_BEDROCK)) {
			if (desState.isOf(Blocks.BEDROCK) && toDestroy.getY() < 10 && toDestroy.getY() > 0) {
				if (random.nextInt(10) == 0 && (toDestroy.getY() > 1 || ((Hashing.murmur3_32().hashInt((31*toDestroy.getX())+toDestroy.getZ()).asInt()&127) > 8))) {
					world.setBlockState(toDestroy, YBlocks.ERODED_BEDROCK.getDefaultState());
				}
			} else {
				float hard = desState.getHardness(world, toDestroy);
				if (hard >= 0 && (random.nextFloat()*20) > hard) {
					world.breakBlock(toDestroy, false);
				}
			}
		}
	}

}
