package com.unascribed.yttr.compat.rei;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class PistonSmashingEntry implements RecipeDisplay {

	private final Identifier id;
	private final List<Block> input;
	private final List<Block> catalysts;
	private final EntryStack output;
	private final int cloudColor;
	private final EntryStack cloudOutput;
	
	public PistonSmashingEntry(Identifier id, List<Block> input, List<Block> catalysts, EntryStack output, int cloudColor, EntryStack cloudOutput) {
		this.id = id;
		this.input = input;
		this.catalysts = catalysts;
		this.output = output;
		this.cloudColor = cloudColor;
		this.cloudOutput = cloudOutput;
	}
	
	public List<Block> getInput() {
		return input;
	}
	
	public List<Block> getCatalysts() {
		return catalysts;
	}
	
	public EntryStack getOutput() {
		return output;
	}
	
	public int getCloudColor() {
		return cloudColor;
	}
	
	public EntryStack getCloudOutput() {
		return cloudOutput;
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Lists.newArrayList(Lists.transform(input, EntryStack::create), Lists.transform(catalysts, EntryStack::create));
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(Lists.newArrayList(output, cloudOutput));
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return PistonSmashingCategory.ID;
	}
	
	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return Optional.of(id);
	}

}
