package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import com.google.common.collect.Iterables;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.recipe.Ingredient;

@Mixin(FireworkStarRecipe.class)
public class MixinFireworkStarRecipe implements SpecialInputsRecipe {

	@Shadow @Final
	private static Ingredient TYPE_MODIFIER;
	@Shadow @Final
	private static Ingredient TRAIL_MODIFIER;
	@Shadow @Final
	private static Ingredient FLICKER_MODIFIER;
	@Shadow @Final
	private static Ingredient GUNPOWDER;
	
	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return GUNPOWDER.test(input);
		if (TYPE_MODIFIER.test(input)) {
			return !Iterables.any(Yttr.asListExcluding(inv, slot), TYPE_MODIFIER::test);
		} else if (TRAIL_MODIFIER.test(input)) {
			return !Iterables.any(Yttr.asListExcluding(inv, slot), TRAIL_MODIFIER::test);
		} else if (FLICKER_MODIFIER.test(input)) {
			return !Iterables.any(Yttr.asListExcluding(inv, slot), FLICKER_MODIFIER::test);
		} else if (input.getItem() instanceof DyeItem) {
			return true;
		}
		return false;
	}
	
}
