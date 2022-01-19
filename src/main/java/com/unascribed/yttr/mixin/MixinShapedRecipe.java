package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.recipe.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {

	@ModifyConstant(constant=@Constant(intValue=3), method="getPattern", require=0)
	private static int mojangAreCowardsAndSmellBad(int orig) {
		return 999;
	}
	
}
