package com.unascribed.yttr.mixin.void_fluid.client;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.SuitScreen;
import com.unascribed.yttr.init.YTags;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(at=@At("HEAD"), method="renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", cancellable=true)
	public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null && ((mc.player.isSubmergedIn(YTags.Fluid.VOID) && mc.options.getPerspective() == Perspective.FIRST_PERSON) || mc.currentScreen instanceof SuitScreen)) {
			RenderSystem.clearColor(0, 0, 0, 1);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
			ci.cancel();
		}
	}
	
}
