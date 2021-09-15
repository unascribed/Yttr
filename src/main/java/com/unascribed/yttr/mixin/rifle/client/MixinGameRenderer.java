package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.render.RifleHUDRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(at=@At("RETURN"), method="getFov", cancellable=true)
	private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> ci) {
		if (RifleHUDRenderer.scopeTime > 0) {
			float target = 5;
			float res = target+((float)ci.getReturnValueD()-target)*(1-RifleHUDRenderer.scopeA);
			ci.setReturnValue(Double.valueOf(res));
		}
	}

	@Inject(at=@At("HEAD"), method="renderHand", cancellable=true)
	private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
		if (RifleHUDRenderer.scopeTime > 2) {
			ci.cancel();
		}
	}
	
}
