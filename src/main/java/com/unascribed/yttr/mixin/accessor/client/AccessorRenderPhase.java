package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.RenderPhase;

@Mixin(RenderPhase.class)
public interface AccessorRenderPhase {

	@Accessor("name")
	String yttr$getName();
	
}
