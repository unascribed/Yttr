package com.unascribed.yttr.mixin.ibxm;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;

@Mixin(Sound.class)
public class MixinSound {

	@Shadow @Final
	private Identifier id;
	
	@Inject(at=@At("HEAD"), method="getLocation", cancellable=true)
	public void getLocation(CallbackInfoReturnable<Identifier> ci) {
		if (id.getPath().endsWith(".yttr_xm") || id.getPath().endsWith(".yttr_s3m") || id.getPath().endsWith(".yttr_mod")) {
			ci.setReturnValue(new Identifier(id.getNamespace(), "sounds/"+id.getPath()));
		}
	}
	
}
