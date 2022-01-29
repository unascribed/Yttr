package com.unascribed.yttr.mixin.discovery;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.YLog;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

	@Inject(at=@At("RETURN"), method="deserialize")
	private static void deserialize(Identifier identifier, JsonObject jsonObject, CallbackInfoReturnable<Recipe<?>> ci) {
		if (jsonObject.has("yttr:discovered_via")) {
			Identifier iid = new Identifier(jsonObject.get("yttr:discovered_via").getAsString());
			if (!Registry.ITEM.getOrEmpty(iid).isPresent()) {
				YLog.warn("Recipe {} is discovered by unknown item {}", identifier, iid);
			}
			Yttr.discoveries.put(iid, identifier);
		}
	}
	
}
