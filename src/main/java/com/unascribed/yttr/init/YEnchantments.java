package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.enchant.DisjunctionEnchantment;
import com.unascribed.yttr.content.enchant.VorpalEnchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public class YEnchantments {

	public static final DisjunctionEnchantment DISJUNCTION = new DisjunctionEnchantment();
	public static final VorpalEnchantment VORPAL = new VorpalEnchantment();
	
	public static void init() {
		Yttr.autoRegister(Registry.ENCHANTMENT, YEnchantments.class, Enchantment.class);
	}
	
}
