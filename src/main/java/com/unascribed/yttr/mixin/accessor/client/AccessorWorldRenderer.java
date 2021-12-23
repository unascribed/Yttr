package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public interface AccessorWorldRenderer {

	@Accessor("needsTerrainUpdate")
	void yttr$setNeedsTerrainUpdate(boolean b);
	
}
