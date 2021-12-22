package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Frustum;

@Mixin(Frustum.class)
public interface AccessorFrustum {

	@Invoker("isVisible")
	boolean yttr$isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
	
}
