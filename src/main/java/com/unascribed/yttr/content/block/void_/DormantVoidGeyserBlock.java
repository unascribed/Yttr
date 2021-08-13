package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class DormantVoidGeyserBlock extends Block {

	public DormantVoidGeyserBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!world.getBlockState(pos.up()).isOf(YBlocks.VOID_FILTER)) {
			return YBlocks.VOID_GEYSER.getDefaultState();
		}
		return state;
	}

}
