package com.unascribed.yttr.block.mechanism;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public class ReplicatorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SideyInventory {

	public int seed = ThreadLocalRandom.current().nextInt();
	public ItemStack item = ItemStack.EMPTY;
	public UUID owner;
	
	public double distTmp;
	
	public ReplicatorBlockEntity() {
		super(YBlockEntities.REPLICATOR);
	}
	
	@Override
	public void fromClientTag(CompoundTag tag) {
		seed = tag.getInt("Seed");
		item = ItemStack.fromTag(tag.getCompound("Item"));
	}
	
	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		tag.putInt("Seed", seed);
		tag.put("Item", item.toTag(new CompoundTag()));
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		fromClientTag(tag);
		owner = tag.containsUuid("Owner") ? tag.getUuid("Owner") : null;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		toClientTag(tag);
		if (owner != null) tag.putUuid("Owner", owner);
		return super.toTag(tag);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toClientTag(super.toInitialChunkDataTag());
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		ItemStack copy = item.copy();
		copy.setCount(copy.getMaxCount());
		return copy;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack copy = item.copy();
		copy.setCount(amount);
		return copy;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return item.copy();
	}

	@Override
	public void setStack(int slot, ItemStack stack) {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
	}

}
