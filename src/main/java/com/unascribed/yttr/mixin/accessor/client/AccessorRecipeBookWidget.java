package com.unascribed.yttr.mixin.accessor.client;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookWidget.class)
public interface AccessorRecipeBookWidget {

	@Invoker("refreshInputs")
	void yttr$refreshInputs();
	
}
