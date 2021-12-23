package com.unascribed.yttr.mixin.particlefix;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YttrClient;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

	@Shadow @Final
	private MinecraftClient client;
	@Shadow @Final
	private BufferBuilderStorage bufferBuilders;
	@Shadow
	private ShaderEffect transparencyShader;
	@Shadow
	private ClientWorld world;
	
	@Inject(method="render", at=@At(value="CONSTANT", args="stringValue=blockentities", ordinal=0))
	private void afterEntities(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		if (transparencyShader != null) return;
		world.getProfiler().swap("yttr:opaque_particles");
		YttrClient.onlyRenderNonOpaqueParticles = false;
		YttrClient.onlyRenderOpaqueParticles = true;
		try {
			client.particleManager.renderParticles(matrices, bufferBuilders.getEntityVertexConsumers(), lightmapTextureManager, camera, tickDelta);
		} finally {
			YttrClient.onlyRenderOpaqueParticles = false;
		}
	}
	
	@ModifyConstant(method="render", constant=@Constant(stringValue="particles", ordinal=1))
	private String beforeParticles(String orig) {
		if (transparencyShader != null) return orig;
		YttrClient.onlyRenderNonOpaqueParticles = true;
		return "yttr:translucent_particles";
	}
	
	@Inject(method = "render", at = @At(value="INVOKE", target="net/minecraft/client/particle/ParticleManager.renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V",
				shift=Shift.AFTER))
	private void afterParticles(CallbackInfo ci) {
		YttrClient.onlyRenderNonOpaqueParticles = false;
	}

}
