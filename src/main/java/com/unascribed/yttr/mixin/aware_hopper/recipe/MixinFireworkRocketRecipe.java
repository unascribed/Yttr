package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;
import com.unascribed.yttr.util.MoreIterables;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.Ingredient;

@Mixin(FireworkRocketRecipe.class)
public class MixinFireworkRocketRecipe implements SpecialInputsRecipe {

	@Shadow @Final
	private static Ingredient PAPER;
	@Shadow @Final
	private static Ingredient DURATION_MODIFIER;
	@Shadow @Final
	private static Ingredient FIREWORK_STAR;
	
	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return PAPER.test(input);
		if (FIREWORK_STAR.test(input)) return true;
		if (DURATION_MODIFIER.test(input)) {
			return MoreIterables.count(Yttr.asListExcluding(inv, slot), DURATION_MODIFIER::test) < 3;
		}
		return false;
	}
	
}
