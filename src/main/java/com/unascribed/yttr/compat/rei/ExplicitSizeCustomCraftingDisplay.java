package com.unascribed.yttr.compat.rei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.server.ContainerInfo;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class ExplicitSizeCustomCraftingDisplay implements DefaultCraftingDisplay {

	private final List<List<EntryStack>> input;
	private final List<EntryStack> output;
	private final Recipe<?> possibleRecipe;

	private final int width;
	private final int height;

	public ExplicitSizeCustomCraftingDisplay(Recipe<?> possibleRecipe, List<List<EntryStack>> input, List<EntryStack> output, int width, int height) {
		this.possibleRecipe = possibleRecipe;
		this.input = ImmutableList.copyOf(Lists.transform(input, ImmutableList::copyOf));
		this.output = ImmutableList.copyOf(output);
		this.width = width;
		this.height = height;
	}

	protected Optional<Recipe<?>> getRecipe() {
		return Optional.ofNullable(possibleRecipe);
	}

	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return getRecipe().map(Recipe::getId);
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return input;
	}

	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(output);
	}

	@Override
	public @NotNull List<List<EntryStack>> getRequiredEntries() {
		return input;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Optional<Recipe<?>> getOptionalRecipe() {
		return Optional.ofNullable(possibleRecipe);
	}
	
	@Override
	public List<List<EntryStack>> getOrganisedInputEntries(ContainerInfo<ScreenHandler> containerInfo, ScreenHandler container) {
		if (width == 3 && height == 3) return DefaultCraftingDisplay.super.getOrganisedInputEntries(containerInfo, container);
		return Collections.emptyList(); // it doesn't work at all, and spams the log with errors
	}

}
