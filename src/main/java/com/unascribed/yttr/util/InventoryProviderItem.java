package com.unascribed.yttr.util;

import net.minecraft.item.ItemStack;

import net.minecraft.inventory.Inventory;

public interface InventoryProviderItem {

	Inventory asInventory(ItemStack stack);
	
}
