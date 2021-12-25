package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.crafting.ShatteringRecipe;
import com.unascribed.yttr.crafting.SoakingRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public class YRecipeSerializers {

	public static final LampRecipe.Serializer LAMP_CRAFTING = new LampRecipe.Serializer();
	public static final CentrifugingRecipe.Serializer CENTRIFUGING = new CentrifugingRecipe.Serializer();
	public static final VoidFilteringRecipe.Serializer VOID_FILTERING = new VoidFilteringRecipe.Serializer();
	public static final PistonSmashingRecipe.Serializer PISTON_SMASHING = new PistonSmashingRecipe.Serializer();
	public static final SoakingRecipe.Serializer SOAKING = new SoakingRecipe.Serializer();
	public static final ShatteringRecipe.Serializer SHATTERING = new ShatteringRecipe.Serializer();

	public static void init() {
		Yttr.autoRegister(Registry.RECIPE_SERIALIZER, YRecipeSerializers.class, RecipeSerializer.class);
	}
	
}
