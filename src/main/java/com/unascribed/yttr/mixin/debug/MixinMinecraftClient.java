package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.render.ProfilerRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.ProfileResult;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Inject(at=@At("HEAD"), method="shouldMonitorTickDuration", cancellable=true)
	private void shouldMonitorTickDuration(CallbackInfoReturnable<Boolean> ci) {
		MinecraftClient self = (MinecraftClient)(Object)this;
		if (ProfilerRenderer.enabled && !self.options.hudHidden) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(at=@At("HEAD"), method="drawProfilerResults", cancellable=true)
	private void drawProfilerResults(MatrixStack matrices, ProfileResult profileResult, CallbackInfo ci) {
		if (ProfilerRenderer.enabled) {
			ProfilerRenderer.render(matrices);
			ci.cancel();
		}
	}
	
}
