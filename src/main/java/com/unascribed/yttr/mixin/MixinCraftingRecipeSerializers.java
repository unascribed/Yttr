package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.unascribed.yttr.Yttr;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin({ShapedRecipe.Serializer.class, ShapelessRecipe.Serializer.class})
public class MixinCraftingRecipeSerializers {

	@Inject(at=@At("RETURN"), method="read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/Recipe;")
	public void read(Identifier identifier, JsonObject jsonObject, CallbackInfoReturnable<Recipe<?>> ci) {
		if (jsonObject.has("yttr:sound")) {
			Yttr.craftingSounds.put(identifier, Registry.SOUND_EVENT.get(new Identifier(jsonObject.get("yttr:sound").getAsString())));
		} else {
			Yttr.craftingSounds.remove(identifier);
		}
	}
	
}
