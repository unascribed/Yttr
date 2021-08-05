package com.unascribed.yttr.mixinsupport;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;

public interface YttrWorld {

	boolean yttr$isPhased(BlockPos pos);
	@Nullable UUID yttr$getPhaser(BlockPos pos);
	void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay, @Nullable UUID owner);
	void yttr$removePhaseBlock(BlockPos pos);
	
	void yttr$scheduleRenderUpdate(BlockPos pos);
	
}
