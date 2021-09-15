package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public interface AccessorWorldRenderer {

	@Accessor("needsTerrainUpdate")
	void yttr$setNeedsTerrainUpdate(boolean b);
	
}
