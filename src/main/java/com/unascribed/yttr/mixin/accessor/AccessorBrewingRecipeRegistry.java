package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public interface AccessorBrewingRecipeRegistry {

	@Invoker("registerItemRecipe")
	static void registerItemRecipe(Item input, Item ingredient, Item output) {
		throw new AbstractMethodError();
	}
	
}
