package com.unascribed.yttr.content.block.void_;

import java.util.Random;

import com.unascribed.yttr.init.YFluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ErodedBedrockBlock extends Block {

	public ErodedBedrockBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		YFluids.PURE_VOID.onRandomTick(world, pos, YFluids.PURE_VOID.getDefaultState(), random);
	}

}
