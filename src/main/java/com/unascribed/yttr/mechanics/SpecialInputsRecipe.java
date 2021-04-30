package com.unascribed.yttr.mechanics;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface SpecialInputsRecipe {

	boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input);
	
}
