package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SuspiciousStewRecipe;
import net.minecraft.tag.ItemTags;

@Mixin(SuspiciousStewRecipe.class)
public class MixinSuspiciousStewRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return input.getItem() == Items.BOWL;
		if (slot == 1) return input.getItem() == Items.BROWN_MUSHROOM;
		if (slot == 2) return input.getItem() == Items.RED_MUSHROOM;
		if (slot == 3) return input.getItem().isIn(ItemTags.SMALL_FLOWERS);
		return false;
	}
	
}
