package com.unascribed.yttr.compat.rei;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class SoakingEntry implements RecipeDisplay {

	private final Identifier id;
	private final List<List<EntryStack>> allInputs;
	private final List<List<EntryStack>> input;
	private final List<EntryStack> catalyst;
	private final List<EntryStack> output;
	private final boolean consumesCatalyst;
	
	public SoakingEntry(Identifier id, List<List<EntryStack>> input, List<EntryStack> catalyst, List<EntryStack> output, boolean consumesCatalyst) {
		this.id = id;
		this.input = input;
		this.catalyst = catalyst;
		this.output = output;
		this.consumesCatalyst = consumesCatalyst;
		this.allInputs = Lists.newArrayList();
		this.allInputs.addAll(input);
		this.allInputs.add(catalyst);
	}
	
	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return Optional.of(id);
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return allInputs;
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return ImmutableList.of(output);
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return SoakingCategory.ID;
	}
	
	public List<List<EntryStack>> getInput() {
		return input;
	}
	
	public List<EntryStack> getCatalyst() {
		return catalyst;
	}
	
	public List<EntryStack> getOutput() {
		return output;
	}
	
	public boolean consumesCatalyst() {
		return consumesCatalyst;
	}
	
}
