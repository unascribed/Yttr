package com.unascribed.yttr.mixin.snare;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.gson.JsonObject;
import com.unascribed.yttr.crafting.EntityIngredientEntry;
import com.unascribed.yttr.init.YItems;

import com.google.common.base.Ascii;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Arm;
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
					EntityIngredientEntry eie = ((EntityIngredientEntry)en);
					if (itemStack.getItem() == YItems.SNARE && YItems.SNARE.getEntityType(itemStack) == eie.entityType) {
						if (eie.mainHand != null) {
							boolean leftHanded = itemStack.getSubTag("Contents").getBoolean("LeftHanded");
							ci.setReturnValue(leftHanded == (eie.mainHand == Arm.LEFT));
						} else {
							ci.setReturnValue(true);
						}
					} else {
						ci.setReturnValue(false);
					}
				}
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="entryFromJson", cancellable=true)
	private static void entryFromJson(JsonObject obj, CallbackInfoReturnable<Ingredient.Entry> ci) {
		if (obj.has("yttr:entity")) {
			ci.setReturnValue(new EntityIngredientEntry(
					Registry.ENTITY_TYPE.get(new Identifier(obj.get("yttr:entity").getAsString())),
					obj.has("yttr:main_hand") ? Arm.valueOf(Ascii.toUpperCase(obj.get("yttr:main_hand").getAsString())) : null
				)
			);
		}
	}
	
}
