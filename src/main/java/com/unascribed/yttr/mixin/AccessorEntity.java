package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public interface AccessorEntity {

	@Invoker("getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;")
	Vec3d yttr$callGetRotationVector(float pitch, float yaw);
	
}
