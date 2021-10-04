package com.unascribed.yttr.compat.rei;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.plugin.crafting.DefaultCustomDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class LampDisplay extends DefaultCustomDisplay {

	public LampDisplay(List<List<ItemStack>> input, List<ItemStack> output, Recipe<?> possibleRecipe) {
		super(input, output, possibleRecipe);
	}

	public LampDisplay(List<List<ItemStack>> input, List<ItemStack> output) {
		super(input, output);
	}

	public LampDisplay(Recipe<?> possibleRecipe, List<List<EntryStack>> input, List<EntryStack> output) {
		super(possibleRecipe, input, output);
	}
	
	@Override
	public @NotNull Identifier getRecipeCategory() {
		return LampCraftingCategory.ID;
	}

}
