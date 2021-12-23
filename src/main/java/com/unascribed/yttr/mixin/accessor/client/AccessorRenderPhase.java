package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderPhase;

@Environment(EnvType.CLIENT)
@Mixin(RenderPhase.class)
public interface AccessorRenderPhase {

	@Accessor("name")
	String yttr$getName();
	
}
