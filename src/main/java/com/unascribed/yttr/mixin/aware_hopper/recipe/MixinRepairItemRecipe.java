package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;

@Mixin(RepairItemRecipe.class)
public class MixinRepairItemRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return input.getItem().isDamageable() && input.isDamaged();
		if (slot == 1) return input.getItem().isDamageable() && input.isDamaged() && input.getItem() == inv.getStack(0).getItem();
		return false;
	}
	
}
