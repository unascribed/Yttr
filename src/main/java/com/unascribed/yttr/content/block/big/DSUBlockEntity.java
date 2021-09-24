package com.unascribed.yttr.content.block.big;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.big.DSUBlock.OpenState;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.inventory.HighStackSimpleInventory;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Direction;

public class DSUBlockEntity extends BlockEntity implements DelegatingInventory, BlockEntityClientSerializable, SideyInventory {

	private final HighStackSimpleInventory contents = new HighStackSimpleInventory(9*5);
	
	public int viewers;
	
	public DSUBlockEntity() {
		super(YBlockEntities.DSU);
		contents.addListener((i) -> markDirty());
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.isWithinDistance(player.getPos(), 5);
	}

	public DSUBlockEntity getController() {
		BlockState state = getCachedState();
		if (!(state.getBlock() instanceof DSUBlock)) return this;
		BigBlock b = (BigBlock)state.getBlock();
		int x = state.get(b.X);
		int y = state.get(b.Y);
		int z = state.get(b.Z);
		if (x == 0 && y == 0 && z == 0) return this;
		BlockEntity origin = world.getBlockEntity(pos.add(-x, -y, -z));
		if (!(origin instanceof DSUBlockEntity)) return this;
		return (DSUBlockEntity)origin;
	}
	
	@Override
	public Inventory getDelegateInv() {
		DSUBlockEntity con = getController();
		if (con == this) return contents;
		return con;
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.put("Contents", Yttr.serializeInv(contents));
		return tag;
	}
	
	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		Yttr.deserializeInv(tag.getList("Contents", NbtType.COMPOUND), contents);
	}
	
	@Override
	public void onOpen(PlayerEntity player) {
		DSUBlockEntity con = getController();
		if (con == this) {
			viewers++;
			if (!getCachedState().get(DSUBlock.OPEN).isTrue()) {
				BigBlock.playSound(world, null, pos, getCachedState(), YSounds.DSU_OPEN, SoundCategory.BLOCKS, 1, 1);
				world.setBlockState(pos, getCachedState().with(DSUBlock.OPEN, OpenState.TRUE));
			}
		} else {
			con.onOpen(player);
		}
	}
	
	@Override
	public void onClose(PlayerEntity player) {
		DSUBlockEntity con = getController();
		if (con == this) {
			viewers--;
			if (getCachedState().get(DSUBlock.OPEN).isTrue() && !getCachedState().get(DSUBlock.OPEN).isForced()) {
				BigBlock.playSound(world, null, pos, getCachedState(), YSounds.DSU_CLOSE, SoundCategory.BLOCKS, 1, 1);
				world.setBlockState(pos, getCachedState().with(DSUBlock.OPEN, OpenState.FALSE));
			}
		} else {
			con.onClose(player);
		}
	}

	@Override
	public void fromClientTag(NbtCompound tag) {
		Yttr.deserializeInv(tag.getList("Contents", NbtType.COMPOUND), contents);
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag) {
		tag.put("Contents", Yttr.serializeInv(contents));
		return tag;
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		if (world != null && !world.isClient) {
			sync();
		}
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
