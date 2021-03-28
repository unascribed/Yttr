package com.unascribed.yttr.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.gson.JsonObject;
import com.unascribed.yttr.EntityIngredientEntry;
import com.unascribed.yttr.Yttr;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(Ingredient.class)
public class MixinIngredient {
	
	@Shadow @Final
	private Ingredient.Entry[] entries;
	
	@Inject(at=@At("HEAD"), method="test", cancellable=true)
	public void test(@Nullable ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (itemStack != null) {
			for (Ingredient.Entry en : entries) {
				if (en instanceof EntityIngredientEntry) {
					if (itemStack.getItem() == Yttr.SNARE && Yttr.SNARE.getEntityType(itemStack) == ((EntityIngredientEntry)en).entityType) {
						ci.setReturnValue(true);
					}
				}
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="entryFromJson", cancellable=true)
	private static void entryFromJson(JsonObject obj, CallbackInfoReturnable<Ingredient.Entry> ci) {
		if (obj.has("yttr:entity")) {
			ci.setReturnValue(new EntityIngredientEntry(Registry.ENTITY_TYPE.get(new Identifier(obj.get("yttr:entity").getAsString()))));
		}
	}
	
}
