package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.InRedProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

//TODO: rendering (fullbright constantly)
public class InRedBlock extends Block implements InRedProvider {
	public InRedBlock(Settings settings) {
		super(settings);
	}

	@Override
	public InRedDevice getDevice(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		return () -> InRedLogic.MAX_SIGNAL;
	}
}
