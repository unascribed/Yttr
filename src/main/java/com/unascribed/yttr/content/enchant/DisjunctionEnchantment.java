package com.unascribed.yttr.content.enchant;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class DisjunctionEnchantment extends DamageEnchantment {

	public static final EntityGroup ENDER = new EntityGroup();
	
	public DisjunctionEnchantment() {
		super(Rarity.UNCOMMON, 1, EquipmentSlot.MAINHAND);
	}
	
	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		if (group == ENDER) {
			return level * 3.5f;
		}
		return 0;
	}

}
