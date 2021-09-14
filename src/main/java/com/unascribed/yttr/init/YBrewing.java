package com.unascribed.yttr.init;

import com.unascribed.yttr.mixin.accessor.AccessorBrewingRecipeRegistry;

import net.minecraft.item.Items;

public class YBrewing {

	public static void init() {
		AccessorBrewingRecipeRegistry.registerPotionType(YItems.MERCURIAL_POTION);
		AccessorBrewingRecipeRegistry.registerPotionType(YItems.MERCURIAL_SPLASH_POTION);
		
		AccessorBrewingRecipeRegistry.registerItemRecipe(Items.POTION, YItems.QUICKSILVER, YItems.MERCURIAL_POTION);
		AccessorBrewingRecipeRegistry.registerItemRecipe(Items.SPLASH_POTION, YItems.QUICKSILVER, YItems.MERCURIAL_SPLASH_POTION);
		
		AccessorBrewingRecipeRegistry.registerItemRecipe(YItems.MERCURIAL_POTION, Items.GUNPOWDER, YItems.MERCURIAL_SPLASH_POTION);
	}
	
}
