package com.unascribed.yttr.compat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class CentrifugingEntry implements RecipeDisplay {

	private final Identifier id;
	private final List<EntryStack> input;
	private final List<EntryStack> outputs;
	
	public CentrifugingEntry(Identifier id, List<EntryStack> input, List<EntryStack> outputs) {
		this.id = id;
		this.input = input;
		this.outputs = outputs;
	}
	
	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return Optional.of(id);
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Collections.singletonList(input);
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Lists.transform(outputs, Lists::newArrayList);
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return CentrifugingCategory.ID;
	}
	
}
