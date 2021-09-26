package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.crafting.SoakingRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class YRecipeTypes {

	public static final RecipeType<CentrifugingRecipe> CENTRIFUGING = create("centrifuging");
	public static final RecipeType<VoidFilteringRecipe> VOID_FILTERING = create("void_filtering");
	public static final RecipeType<PistonSmashingRecipe> PISTON_SMASHING = create("piston_smashing");
	public static final RecipeType<SoakingRecipe> SOAKING = create("soaking");

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
