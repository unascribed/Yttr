package com.unascribed.yttr.mixin.diving.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.SuitScreen;
import com.unascribed.yttr.client.SuitSound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

	@Inject(at=@At("HEAD"), method="getAdjustedVolume", cancellable=true)
	private void getAdjustedVolume(SoundInstance soundInstance, CallbackInfoReturnable<Float> ci) {
		if (MinecraftClient.getInstance().currentScreen instanceof SuitScreen && !(soundInstance instanceof SuitSound)) {
			ci.setReturnValue(0f);
		}
	}
	
}
