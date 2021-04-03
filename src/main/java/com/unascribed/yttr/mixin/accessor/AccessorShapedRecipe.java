package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.recipe.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public interface AccessorShapedRecipe {

	@Accessor("group")
	String yttr$getGroup();
	
}
