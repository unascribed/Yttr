package com.unascribed.yttr.mechanics;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class BedrockMender {

	public static void tickChunk(WorldChunk chunk, int randomTickSpeed) {
		// this is mostly unnecessary (the below even catches *headless pistons*) and ruins skyblock
//		if (chunk.getWorld().getServer().getOverworld() == chunk.getWorld()) {
//			BlockPos pos = new BlockPos(chunk.getWorld().random.nextInt(16), 0, chunk.getWorld().random.nextInt(16));
//			if (chunk.getBlockState(pos).isAir()) {
//				chunk.setBlockState(pos, YBlocks.VOID_GEYSER.getDefaultState(), false);
//			}
//		}
	}
	
	public static void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!(world instanceof ServerWorld)) return;
		if (world == world.getServer().getOverworld() && pos.getY() == 0 && newState.getHardness(world, pos) >= 0) {
			world.setBlockState(pos, YBlocks.VOID_GEYSER.getDefaultState());
		}
	}
	
}
