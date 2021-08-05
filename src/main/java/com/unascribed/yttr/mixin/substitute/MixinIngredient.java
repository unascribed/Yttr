package com.unascribed.yttr.mixin.substitute;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.unascribed.yttr.mixinsupport.SetNoSubstitution;

import net.minecraft.recipe.Ingredient;

@Mixin(Ingredient.class)
public abstract class MixinIngredient {
	
	@Shadow @Final
	private Ingredient.Entry[] entries;
	
	@Inject(at=@At("RETURN"), method="entryFromJson")
	private static void entryFromJson(JsonObject obj, CallbackInfoReturnable<Ingredient.Entry> ci) {
		if (obj.has("yttr:no_substitution") && obj.get("yttr:no_substitution").getAsBoolean() && ci.getReturnValue() instanceof SetNoSubstitution) {
			((SetNoSubstitution)ci.getReturnValue()).yttr$setNoSubstitution(true);
		}
	}
	
}
