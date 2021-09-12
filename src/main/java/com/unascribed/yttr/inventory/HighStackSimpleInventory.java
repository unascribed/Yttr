package com.unascribed.yttr.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class HighStackSimpleInventory extends SimpleInventory {

	public HighStackSimpleInventory(int size) {
		super(size);
	}

	public HighStackSimpleInventory(ItemStack... items) {
		super(items);
	}
	
	@Override
	public int getMaxCountPerStack() {
		return 1024;
	}

}
