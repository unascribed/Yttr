package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.render.RifleHUDRenderer;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		if (RifleHUDRenderer.scopeTime > 0) {
			RifleHUDRenderer.render(matrices, tickDelta);
			ci.cancel();
		}
	}
	
}
