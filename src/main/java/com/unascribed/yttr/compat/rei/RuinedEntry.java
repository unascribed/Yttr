package com.unascribed.yttr.compat.rei;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class RuinedEntry implements RecipeDisplay {

	private final Identifier id;
	private final EntryStack result;
	private final Set<Integer> emptySlots;
	
	public RuinedEntry(Identifier id, EntryStack result, Set<Integer> emptySlots) {
		this.id = id;
		this.result = result;
		this.emptySlots = emptySlots;
	}
	
	public Identifier getId() {
		return id;
	}
	
	public EntryStack getResult() {
		return result;
	}
	
	public Set<Integer> getEmptySlots() {
		return emptySlots;
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return Collections.emptyList();
	}
	
	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return Collections.singletonList(Collections.singletonList(result));
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return RuinedCategory.ID;
	}
	
}
