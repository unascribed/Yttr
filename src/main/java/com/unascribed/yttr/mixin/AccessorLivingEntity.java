package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {

	@Invoker("getSoundPitch")
	float yttr$getSoundPitch();
	@Invoker("getSoundVolume")
	float yttr$getSoundVolume();
	
	@Invoker("playHurtSound")
	void yttr$playHurtSound(DamageSource source);
	
}
