package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class YRecipeTypes {

	public static final RecipeType<CentrifugingRecipe> CENTRIFUGING = create("centrifuging");

	public static void init() {
		Yttr.autoRegister(Registry.RECIPE_TYPE, YRecipeTypes.class, RecipeType.class);
	}
	
	private static <T extends Recipe<?>> RecipeType<T> create(String id) {
		String fullId = "yttr:"+id;
		return new RecipeType<T>() {
			@Override
			public String toString() {
				return fullId;
			}
		};
	}
	
}
