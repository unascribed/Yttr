package com.unascribed.yttr.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface DelegatingInventory extends Inventory {

	Inventory getDelegateInv();

	@Override
	default void clear() {
		getDelegateInv().clear();
	}

	@Override
	default int size() {
		return getDelegateInv().size();
	}

	@Override
	default boolean isEmpty() {
		return getDelegateInv().isEmpty();
	}

	@Override
	default ItemStack getStack(int slot) {
		return getDelegateInv().getStack(slot);
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		return getDelegateInv().removeStack(slot, amount);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return getDelegateInv().removeStack(slot);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
		getDelegateInv().setStack(slot, stack);
	}
	
	@Override
	default int getMaxCountPerStack() {
		return getDelegateInv().getMaxCountPerStack();
	}
	
}
