package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShieldDecorationRecipe;

@Mixin(ShieldDecorationRecipe.class)
public class MixinShieldDecorationRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return input.getItem() == Items.SHIELD && input.getSubTag("BlockEntityTag") == null;
		if (slot == 1) return input.getItem() instanceof BannerItem;
		return false;
	}
	
}
