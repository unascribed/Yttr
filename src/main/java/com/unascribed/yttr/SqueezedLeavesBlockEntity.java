package com.unascribed.yttr;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class SqueezedLeavesBlockEntity extends BlockEntity {

	public int step;
	public int decayTime;
	
	public long squeezeBegin = -1;
	
	public SqueezedLeavesBlockEntity() {
		super(Yttr.SQUEEZED_LEAVES_ENTITY);
	}
	
	public void step() {
		decayTime = 0;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.putInt("Step", step);
		tag.putInt("DecayTime", decayTime);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		step = tag.getInt("Step");
		decayTime = tag.getInt("DecayTime");
	}
	

}
