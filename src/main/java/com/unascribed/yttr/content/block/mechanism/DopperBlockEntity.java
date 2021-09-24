package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mixin.accessor.AccessorBlockEntity;
import com.unascribed.yttr.mixin.accessor.AccessorHopperBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DopperBlockEntity extends HopperBlockEntity {

	private boolean tock = false;
	
	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			AccessorHopperBlockEntity acc = (AccessorHopperBlockEntity)this;
			acc.yttr$setTransferCooldown(acc.yttr$getTransferCooldown()-1);
			acc.yttr$setLastTickTime(world.getTime());
			if (!acc.yttr$needsCooldown()) {
				acc.yttr$setCooldown(0);
				BlockState realState = getCachedState();
				try {
					if (tock) {
						((AccessorBlockEntity)this).yttr$setCachedState(realState.with(DopperBlock.FACING, realState.get(DopperBlock.FACING).getOpposite()));
					}
					if (acc.yttr$insertAndExtract(() -> extract(this))) {
						tock = !tock;
					}
				} finally {
					((AccessorBlockEntity)this).yttr$setCachedState(realState);
				}
			}
		}
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag = super.writeNbt(tag);
		tag.putBoolean("Tock", tock);
		return tag;
	}
	
	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		tock = tag.getBoolean("Tock");
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return YBlockEntities.DOPPER;
	}
	
	@Override
	protected Text getContainerName() {
		return new TranslatableText("block.yttr.dopper");
	}
	
}
