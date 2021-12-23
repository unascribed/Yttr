package com.unascribed.yttr.mixin.accessor.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public interface AccessorEntityRendererDispatcher {

	@Accessor("renderers")
	Map<EntityType<?>, EntityRenderer<?>> yttr$getRenderers();
	
}
