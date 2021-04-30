package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BannerDuplicateRecipe;

@Mixin(BannerDuplicateRecipe.class)
public class MixinBannerDuplicateRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return input.getItem() instanceof BannerItem;
		if (input.getItem() instanceof BannerItem && inv.getStack(0).getItem() instanceof BannerItem) {
			return ((BannerItem)input.getItem()).getColor() == ((BannerItem)inv.getStack(0).getItem()).getColor();
		}
		return false;
	}
	
}
