package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.block.FlopperBlock;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class FlopperBlockEntity extends HopperBlockEntity {

	private BlockState realState;
	
	@Override
	public void tick() {
		realState = getCachedState();
		try {
			((AccessorBlockEntity)this).yttr$setCachedState(realState.with(FlopperBlock.FACING, Direction.DOWN));
			super.tick();
		} finally {
			((AccessorBlockEntity)this).yttr$setCachedState(realState);
			realState = null;
		}
	}
	
	public BlockState getRealState() {
		return realState == null ? getCachedState() : realState;
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return YBlockEntities.FLOPPER;
	}
	
	@Override
	protected Text getContainerName() {
		return new TranslatableText("block.yttr.flopper");
	}
	
	@Override
	public VoxelShape getInputAreaShape() {
		return YBlocks.FLOPPER.getFunnelShape(getRealState());
	}
	
}
