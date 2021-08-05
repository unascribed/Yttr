package com.unascribed.yttr.mixin.potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.mixin.accessor.AccessorStatusEffectInstance;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

	@Shadow
	public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
	@Shadow
	public abstract boolean hasStatusEffect(StatusEffect effect);
	@Shadow
	public abstract boolean addStatusEffect(StatusEffectInstance effect);
	
	@Inject(at=@At("HEAD"), method="addStatusEffect", cancellable=true)
	public void addStatusEffect(StatusEffectInstance in, CallbackInfoReturnable<Boolean> ci) {
		if (in == null) return;
		if (hasStatusEffect(YStatusEffects.POTION_SICKNESS)) {
			if (in.getEffectType().getType() != StatusEffectType.HARMFUL) {
				ci.setReturnValue(false);
			}
		}
		if (in.getEffectType() != YStatusEffects.DELICACENESS) {
			StatusEffectInstance d = getStatusEffect(YStatusEffects.DELICACENESS);
			if (d != null) {
				float potency = (d.getAmplifier()+1)*0.10f;
				float mod = 1;
				if (in.getEffectType().getType() == StatusEffectType.BENEFICIAL) {
					mod = 1+potency;
				} else if (in.getEffectType().getType() == StatusEffectType.HARMFUL) {
					mod = 1-potency;
				}  else {
					return;
				}
				((AccessorStatusEffectInstance)in).yttr$setDuration((int)(in.getDuration()*mod));
			}
		}
	}
	
	private StatusEffectInstance yttr$origPotionSickness;
	
	@Inject(at=@At("HEAD"), method="clearStatusEffects")
	public void clearStatusEffectsHead(CallbackInfoReturnable<Boolean> ci) {
		yttr$origPotionSickness = getStatusEffect(YStatusEffects.POTION_SICKNESS);
	}
	
	@Inject(at=@At("RETURN"), method="clearStatusEffects")
	public void clearStatusEffectsReturn(CallbackInfoReturnable<Boolean> ci) {
		if (yttr$origPotionSickness != null) {
			addStatusEffect(yttr$origPotionSickness);
			yttr$origPotionSickness = null;
		}
	}
	
}
