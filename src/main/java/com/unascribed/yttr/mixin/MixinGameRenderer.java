package com.unascribed.yttr.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(at=@At("HEAD"), method="renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", cancellable=true)
	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null && mc.player.isSubmergedIn(Yttr.VOID_TAG)) {
			RenderSystem.clearColor(0, 0, 0, 1);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
			ci.cancel();
		}
	}
	
}
