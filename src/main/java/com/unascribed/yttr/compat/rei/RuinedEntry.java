package com.unascribed.yttr.compat.rei;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

public class RuinedEntry implements RecipeDisplay {

	private final Identifier id;
	private final EntryStack result;
	
	public RuinedEntry(Identifier id, EntryStack result) {
		this.id = id;
		this.result = result;
	}
	
	public Identifier getId() {
		return id;
	}
	
	public EntryStack getResult() {
		return result;
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
