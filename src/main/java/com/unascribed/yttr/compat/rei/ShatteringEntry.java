package com.unascribed.yttr.compat.rei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.unascribed.yttr.crafting.ShatteringRecipe;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class ShatteringEntry implements RecipeDisplay {

	private final Identifier id;
	private final List<EntryStack> input;
	private final EntryStack output;
	public final boolean exclusive;
	
	public ShatteringEntry(Recipe<CraftingInventory> recipe) {
		this.id = recipe.getId();
		this.input = EntryStack.ofIngredient(recipe.getIngredients().get(0));
		this.output = EntryStack.create(recipe.getOutput());
		this.exclusive = recipe instanceof ShatteringRecipe;
	}
	
	public ShatteringEntry(Identifier id, List<EntryStack> input,
			EntryStack output, boolean exclusive) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.exclusive = exclusive;
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return ShatteringCategory.ID;
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Collections.singletonList(input);
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(Collections.singletonList(output));
	}
	
	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return Optional.of(id);
	}

}
