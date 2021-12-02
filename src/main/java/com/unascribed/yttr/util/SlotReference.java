package com.unascribed.yttr.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class SlotReference {

	public final Inventory inventory;
	public final int slot;
	
	public SlotReference(Inventory inventory, int slot) {
		this.inventory = inventory;
		this.slot = slot;
	}
	
	public ItemStack getStack() {
		return inventory.getStack(slot);
	}
	
	public void setStack(ItemStack stack) {
		inventory.setStack(slot, stack);
	}
	
	public boolean isEmpty() {
		return getStack().isEmpty();
	}
	
}
