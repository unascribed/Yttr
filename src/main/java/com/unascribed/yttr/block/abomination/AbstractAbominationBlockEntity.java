package com.unascribed.yttr.block.abomination;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractAbominationBlockEntity extends BlockEntity implements Tickable {

	public int age;
	
	public float headYaw;
	public float prevHeadYaw;
	public float headPitch;
	public float prevHeadPitch;
	
	public AbstractAbominationBlockEntity(BlockEntityType<? extends AbstractAbominationBlockEntity> type) {
		super(type);
	}
	
	public boolean isSuffocating() {
		BlockPos headBlock = new BlockPos(getHeadPos());
		if (headBlock.equals(pos)) return false;
		return world.getBlockState(headBlock).shouldSuffocate(world, headBlock);
	}
	
	public abstract Vec3d getHeadPos();
	
	@Override
	public void tick() {
		age++;
		
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.putFloat("HeadYaw", headYaw);
		tag.putFloat("HeadPitch", headPitch);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		headYaw = prevHeadYaw = (tag.contains("Yaw") ? tag.getFloat("Yaw") : tag.getFloat("HeadYaw"));
		headPitch = prevHeadPitch = (tag.contains("Pitch") ? tag.getFloat("Pitch") : tag.getFloat("HeadPitch"));
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag.putFloat("HeadYaw", headYaw);
		tag.putFloat("HeadPitch", headPitch);
		return tag;
	}
	
}
