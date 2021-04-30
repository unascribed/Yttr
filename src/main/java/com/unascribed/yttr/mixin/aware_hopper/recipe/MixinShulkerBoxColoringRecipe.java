package com.unascribed.yttr.mixin.aware_hopper.recipe;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mechanics.SpecialInputsRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;

@Mixin(ShulkerBoxColoringRecipe.class)
public class MixinShulkerBoxColoringRecipe implements SpecialInputsRecipe {

	@Override
	public boolean yttr$isInputValid(Inventory inv, int slot, ItemStack input) {
		if (slot == 0) return Block.getBlockFromItem(input.getItem()) instanceof ShulkerBoxBlock;
		if (slot == 1) return input.getItem() instanceof DyeItem;
		return false;
	}
	
}
