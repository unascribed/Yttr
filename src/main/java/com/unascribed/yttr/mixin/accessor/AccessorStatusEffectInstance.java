package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(StatusEffectInstance.class)
public interface AccessorStatusEffectInstance {

	@Accessor("duration")
	void yttr$setDuration(int duration);
	
}
