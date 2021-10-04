package com.unascribed.yttr.compat.rei;

import org.jetbrains.annotations.NotNull;

import com.unascribed.yttr.init.YBlocks;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingCategory;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class LampCraftingCategory extends DefaultCraftingCategory {

	public static final Identifier ID = new Identifier("yttr", "lamp_crafting");
	
    @Override
    public @NotNull Identifier getIdentifier() {
        return ID;
    }
    
    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(YBlocks.LAMP);
    }
    
    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate("category.yttr.lamp_crafting");
    }
	
}
