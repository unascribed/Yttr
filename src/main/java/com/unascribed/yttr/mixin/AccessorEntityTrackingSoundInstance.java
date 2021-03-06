package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;

@Mixin(EntityTrackingSoundInstance.class)
public interface AccessorEntityTrackingSoundInstance {

	@Accessor("entity")
	Entity getEntity();
	
}
