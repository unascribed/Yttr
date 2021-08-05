package com.unascribed.yttr.mixinsupport;

import com.unascribed.yttr.Substitutes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

public class UltrapureBonus {

	public static void handleCraft(CraftingInventory inv, ItemStack out) {
		if (!out.isDamageable()) return;
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
