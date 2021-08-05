package com.unascribed.yttr.mixin.ultrapure_bonus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Substitutes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {

	@Inject(at=@At("RETURN"), method="craft(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/item/ItemStack;")
	public void craft(CraftingInventory inv, CallbackInfoReturnable<ItemStack> ci) {
		if (!ci.getReturnValue().isDamageable()) return;
		boolean anyPure = false;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (Substitutes.getPrime(stack.getItem()) != null) {
				// this is an ultrapure resource
				anyPure = true;
			} else if (Substitutes.getSubstitute(stack.getItem()) != null) {
				// this is an impure resource
				return;
			}
		}
		if (anyPure) {
			ItemStack out = ci.getReturnValue();
			if (!out.hasCustomName()) {
				out.setCustomName(new TranslatableText("item.yttr.ultrapure_tool.prefix", out.getName()).setStyle(Style.EMPTY.withItalic(false)));
			}
			if (!out.hasTag()) {
				out.setTag(new CompoundTag());
			}
			out.getTag().putInt("yttr:DurabilityBonus", out.getTag().getInt("yttr:DurabilityBonus")+1);
		}
	}


}
