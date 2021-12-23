package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

@Environment(EnvType.CLIENT)
@Mixin(VertexBuffer.class)
public interface AccessorVertexBuffer {

	@Accessor("vertexCount")
	int yttr$getVertexCount();
	
}
