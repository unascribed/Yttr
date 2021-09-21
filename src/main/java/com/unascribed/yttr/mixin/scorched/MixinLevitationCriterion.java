package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(LevitationCriterion.class)
public class MixinLevitationCriterion {

	@Inject(at=@At("HEAD"), method="trigger", cancellable=true)
	public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration, CallbackInfo ci) {
		StatusEffectInstance se = player.getStatusEffect(StatusEffects.LEVITATION);
		if (se != null && se.getAmplifier() > 0) {
			ci.cancel();
		}
	}
	
}
