package com.unascribed.yttr.compat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class PistonSmashingEntry implements RecipeDisplay {

	private final EntryStack input;
	private final EntryStack output;
	private final List<EntryStack> catalysts;
	
	public PistonSmashingEntry(EntryStack input, EntryStack output, EntryStack... catalysts) {
		this.input = input;
		this.output = output;
		this.catalysts = Arrays.asList(catalysts);
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Collections.singletonList(Collections.singletonList(input));
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getRequiredEntries() {
		return Collections.singletonList(catalysts);
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(Collections.singletonList(output));
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return PistonSmashingCategory.ID;
	}

}
