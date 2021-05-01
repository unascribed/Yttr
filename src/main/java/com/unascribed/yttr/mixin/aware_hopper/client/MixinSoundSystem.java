package com.unascribed.yttr.mixin.aware_hopper.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

	@Inject(at=@At("HEAD"), method="play(Lnet/minecraft/client/sound/SoundInstance;)V", cancellable=true)
	public void play(SoundInstance si, CallbackInfo ci) {
		if (Yttr.lessCreepyAwareHopper && si != null && si.getId().getNamespace().equals("yttr")) {
			switch (si.getId().getPath()) {
				case "aware_hopper_ambient":
				case "aware_hopper_break":
				case "aware_hopper_scream":
				case "craft_aware_hopper":
					ci.cancel();
					break;
				default:
			}
		}
	}
	
}
