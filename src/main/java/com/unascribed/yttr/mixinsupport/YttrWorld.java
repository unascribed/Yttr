package com.unascribed.yttr.mixinsupport;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public interface YttrWorld {

	boolean yttr$isPhased(int x, int y, int z);
	boolean yttr$isPhased(BlockPos pos);
	boolean yttr$isPhased(ChunkPos chunkPos, BlockPos pos);
	@Nullable UUID yttr$getPhaser(BlockPos pos);
	void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay, @Nullable UUID owner);
	void yttr$removePhaseBlock(BlockPos pos);
	
	void yttr$scheduleRenderUpdate(BlockPos pos);
	
	void yttr$setUnmask(boolean unmask);
	
}
