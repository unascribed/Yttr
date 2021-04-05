package com.unascribed.yttr;

import net.minecraft.util.math.BlockPos;

public interface EffectorWorld {

	boolean yttr$isPhased(BlockPos pos);
	void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay);
	void yttr$removePhaseBlock(BlockPos pos);
	
	void yttr$scheduleRenderUpdate(BlockPos pos);
	
}
