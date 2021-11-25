package com.unascribed.yttr.inred;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface EncoderScannable {
	int getEncoderValue(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom);
}
