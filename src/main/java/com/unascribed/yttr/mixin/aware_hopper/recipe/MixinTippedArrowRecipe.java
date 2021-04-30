package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.TippedArrowRecipe;

@Mixin(TippedArrowRecipe.class)
public class MixinTippedArrowRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 4) return input.getItem() == Items.LINGERING_POTION;
		return input.getItem() == Items.ARROW;
	}
	
}
