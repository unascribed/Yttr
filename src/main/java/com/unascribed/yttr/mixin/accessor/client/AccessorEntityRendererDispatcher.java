package com.unascribed.yttr.mixin.accessor.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;

@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRendererDispatcher {

	@Accessor("renderers")
	Map<EntityType<?>, EntityRenderer<?>> yttr$getRenderers();
	
}
