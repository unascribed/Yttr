package com.unascribed.yttr.inred;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface MultimeterProbeProvider {
	Text getProbeMessage(BlockView world, BlockPos pos, BlockState state);
}
