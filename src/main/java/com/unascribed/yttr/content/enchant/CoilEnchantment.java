package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.init.YItems;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class CoilEnchantment extends Enchantment {

	public CoilEnchantment(Rarity weight) {
		super(weight, EnchantmentTarget.BREAKABLE, new EquipmentSlot[0]);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return YItems.CUPROSTEEL_COIL.is(stack.getItem());
	}

}
