package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.render.ProfilerRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public class MixinKeyBinding {

	@Inject(at=@At("HEAD"), method="onKeyPressed")
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
		ProfilerRenderer.handleKey(key);
	}
	
}
