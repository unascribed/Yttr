package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.effect.BleedingStatusEffect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.registry.Registry;

public class YStatusEffects {

	public static final StatusEffect DELICACENESS = new StatusEffect(StatusEffectType.BENEFICIAL, 0xA68FE0) {};
	public static final StatusEffect BLEEDING = new BleedingStatusEffect(StatusEffectType.HARMFUL, 0xFF0606);
	public static final StatusEffect POTION_SICKNESS = new StatusEffect(StatusEffectType.HARMFUL, 0xC8C6E2) {};

	public static void init() {
		Yttr.autoRegister(Registry.STATUS_EFFECT, YStatusEffects.class, StatusEffect.class);
	}

}
