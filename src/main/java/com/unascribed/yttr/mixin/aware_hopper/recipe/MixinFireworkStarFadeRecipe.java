package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.FireworkStarFadeRecipe;
import net.minecraft.recipe.Ingredient;

@Mixin(FireworkStarFadeRecipe.class)
public class MixinFireworkStarFadeRecipe implements SpecialInputsRecipe {

	@Shadow @Final
	private static Ingredient INPUT_STAR;
	
	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return INPUT_STAR.test(input);
		return input.getItem() instanceof DyeItem;
	}
	
}
