package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.render.RifleHUDRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class MixinEntity {

	@Redirect(at=@At(value="FIELD", target="net/minecraft/entity/Entity.renderDistanceMultiplier:D"), method="shouldRender(D)Z", require=0)
	private double getRenderDistanceMultiplier() {
		return Entity.getRenderDistanceMultiplier();
	}
	
	@Inject(at=@At("RETURN"), method="getRenderDistanceMultiplier", cancellable=true)
	private static void getRenderDistanceMultiplier(CallbackInfoReturnable<Double> ci) {
		if (RifleHUDRenderer.scopeTime > 0) {
			ci.setReturnValue(ci.getReturnValueD()*(1+(RifleHUDRenderer.scopeA*5)));
		}
	}
	
}
