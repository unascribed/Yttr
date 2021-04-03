package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvent;

@Mixin(MobEntity.class)
public interface AccessorMobEntity {

	@Invoker("getAmbientSound")
	SoundEvent yttr$getAmbientSound();
	
}
