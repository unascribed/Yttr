package com.unascribed.yttr.compat.rei;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ContinuityEntry implements RecipeDisplay {
    
    private List<EntryStack> entries;
    
    public ContinuityEntry(Collection<ItemStack> entries) {
        this.entries = EntryStack.ofItemStacks(entries);
    }
    
    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return Collections.emptyList();
    }
    
    public List<EntryStack> getEntries() {
        return entries;
    }
    
    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return Collections.singletonList(entries);
    }
    
    @Override
    public @NotNull Identifier getRecipeCategory() {
        return ContinuityCategory.ID;
    }
}
