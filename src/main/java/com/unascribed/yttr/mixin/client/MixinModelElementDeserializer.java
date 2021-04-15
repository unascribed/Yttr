package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;

import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.util.JsonHelper;

@Mixin(ModelElement.Deserializer.class)
public class MixinModelElementDeserializer {

	@Inject(at=@At("HEAD"), method="deserializeRotation", cancellable=true)
	private void deserializeRotation(JsonObject object, CallbackInfoReturnable<Float> ci) {
		if (object.has("yttr:unlock_angle") && object.get("yttr:unlock_angle").getAsBoolean()) {
			ci.setReturnValue(JsonHelper.getFloat(object, "angle"));
		}
	}
	
}
