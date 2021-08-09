package com.unascribed.yttr.compat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class VoidFilteringEntry implements RecipeDisplay {

	private final Identifier id;
	private final EntryStack output;
	private final float chance;
	
	public VoidFilteringEntry(Identifier id, EntryStack output, float chance) {
		this.id = id;
		this.output = output;
		this.chance = chance;
	}
	
	public EntryStack getOutput() {
		return output;
	}
	
	public float getChance() {
		return chance;
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Collections.emptyList();
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(Collections.singletonList(output));
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return VoidFilteringCategory.ID;
	}
	
	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return Optional.of(id);
	}

}
