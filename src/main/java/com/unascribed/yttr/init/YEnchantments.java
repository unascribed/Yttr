package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.enchant.DisjunctionEnchantment;
import com.unascribed.yttr.content.enchant.SpringingEnchantment;
import com.unascribed.yttr.content.enchant.StabilizationEnchantment;
import com.unascribed.yttr.content.enchant.VorpalEnchantment;
import com.unascribed.yttr.util.LatchReference;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public class YEnchantments {

	public static final DisjunctionEnchantment DISJUNCTION = new DisjunctionEnchantment();
	public static final VorpalEnchantment VORPAL = new VorpalEnchantment();
	
	public static final LatchReference<SpringingEnchantment> SPRINGING = YLatches.create();
	public static final LatchReference<StabilizationEnchantment> STABILIZATION = YLatches.create();
	
	public static void init() {
		Yttr.autoRegister(Registry.ENCHANTMENT, YEnchantments.class, Enchantment.class);
	}
	
}
