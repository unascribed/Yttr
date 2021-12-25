package com.unascribed.yttr.compat.rei;

import java.util.List;
import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class LampDisplay extends ExplicitSizeCustomCraftingDisplay {

	public LampDisplay(Recipe<?> possibleRecipe, List<List<EntryStack>> input, List<EntryStack> output, int width, int height) {
		super(possibleRecipe, input, output, width, height);
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return LampCraftingCategory.ID;
	}

}
