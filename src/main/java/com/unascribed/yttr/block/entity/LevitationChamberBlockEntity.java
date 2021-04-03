package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.init.YBlockEntities;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class LevitationChamberBlockEntity extends BlockEntity implements Tickable, SidedInventory, NamedScreenHandlerFactory {

	private final SimpleInventory inv = new SimpleInventory(5);
	
	public int age;
	private int cooldown = 0;
	
	public LevitationChamberBlockEntity() {
		super(YBlockEntities.LEVITATION_CHAMBER);
		inv.addListener((i) -> markDirty());
	}
	
	@Override
	public void tick() {
		age++;
		if (cooldown-- <= 0 && !isEmpty()) {
			for (int i = 0; i < size(); i++) {
				ItemStack is = getStack(i);
				if (!is.isEmpty()) {
					ItemStack transfer = is.copy();
					transfer.setCount(1);
					if (ChuteBlockEntity.transfer(world, pos, Direction.UP, transfer, false)) {
						is.decrement(1);
						setStack(i, is);
						cooldown = 4;
						break;
					}
				}
			}
		}
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		inv.readTags(tag.getList("Inventory", NbtType.COMPOUND));
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.put("Inventory", inv.getTags());
		tag.putInt("Cooldown", cooldown);
		return tag;
	}

	@Override
	public ItemStack getStack(int slot) {
		return inv.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return inv.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return inv.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv.setStack(slot, stack);
	}

	@Override
	public int size() {
		return inv.size();
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return inv.canPlayerUse(player);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return dir == Direction.UP;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return dir != Direction.UP;
	}
	
	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[] {0,1,2,3,4};
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new HopperScreenHandler(syncId, inv, this);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("block.yttr.levitation_chamber");
	}
	
}
