package com.unascribed.yttr.mixin.only_if;

import java.util.Iterator;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.Yttr;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(JsonDataLoader.class)
public class MixinJsonDataLoader {

	@Inject(at=@At("RETURN"), method="prepare")
	protected void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<Map<Identifier, JsonElement>> ci) {
		Iterator<JsonElement> iter = ci.getReturnValue().values().iterator();
		while (iter.hasNext()) {
			JsonElement ele = iter.next();
			if (ele instanceof JsonObject && ele.getAsJsonObject().has("yttr:only_if")) {
				String when = ele.getAsJsonObject().get("yttr:only_if").getAsString();
				boolean active = false;
				if ("copper_fallback".equals(when)) {
					active = Yttr.COPPER_FALLBACK_ACTIVE;
				}
				if (!active) {
					iter.remove();
				}
			}
		}
	}
	
}
