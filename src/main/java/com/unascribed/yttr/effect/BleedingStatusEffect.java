package com.unascribed.yttr.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class BleedingStatusEffect extends StatusEffect {
	private static final DamageSource SOURCE = new DamageSource("yttr.bleeding") {{
		setBypassesArmor();
		setUnblockable();
	}};
	
	public BleedingStatusEffect(StatusEffectType type, int color) {
		super(type, color);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return duration % Math.max(1, 40-(amplifier*5)) == 0;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		int oldHurtTime = entity.hurtTime;
		entity.hurtTime = -20;
		entity.damage(SOURCE, (amplifier/4)+1);
		entity.hurtTime = oldHurtTime;
	}
}