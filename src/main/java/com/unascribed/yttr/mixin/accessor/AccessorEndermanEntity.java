package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.mob.EndermanEntity;

@Mixin(EndermanEntity.class)
public interface AccessorEndermanEntity {

	@Invoker("teleportTo")
	boolean yttr$teleportTo(double x, double y, double z);
	
}
