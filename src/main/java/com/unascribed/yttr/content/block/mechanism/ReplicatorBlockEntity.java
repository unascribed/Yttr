package com.unascribed.yttr.content.block.mechanism;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.client.render.ReplicatorRenderer;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

public class ReplicatorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SideyInventory {

	public int seed = ThreadLocalRandom.current().nextInt();
	public ItemStack item = ItemStack.EMPTY;
	public UUID owner;
	
	public double distTmp;
	
	public int clientAge = 0;
	public int removedTicks = 0;
	
	private boolean addedClient = false;
	
	public ReplicatorBlockEntity() {
		super(YBlockEntities.REPLICATOR);
	}
	
	@Environment(EnvType.CLIENT)
	public void clientTick() {
		clientAge++;
		if (isRemoved()) {
			addedClient = false;
			removedTicks++;
		} else if (!addedClient) {
			addedClient = true;
			ReplicatorRenderer.replicators.add(this);
		}
	}
	
	@Override
	public void markRemoved() {
		super.markRemoved();
		if (world.isClient) {
			removeClient();
		}
	}
	
	@Environment(EnvType.CLIENT)
	private void removeClient() {
		ReplicatorRenderer.replicators.remove(this);
		ReplicatorRenderer.removing.add(this);
	}

	@Override
	public void fromClientTag(NbtCompound tag) {
		seed = tag.getInt("Seed");
		item = ItemStack.fromNbt(tag.getCompound("Item"));
	}
	
	@Override
	public NbtCompound toClientTag(NbtCompound tag) {
		tag.putInt("Seed", seed);
		tag.put("Item", item.writeNbt(new NbtCompound()));
		return tag;
	}
	
	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		fromClientTag(tag);
		owner = tag.containsUuid("Owner") ? tag.getUuid("Owner") : null;
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		toClientTag(tag);
		if (owner != null) tag.putUuid("Owner", owner);
		return super.writeNbt(tag);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return toClientTag(super.toInitialChunkDataNbt());
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
