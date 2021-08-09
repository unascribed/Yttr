package com.unascribed.yttr.compat;

import com.unascribed.yttr.block.device.VoidFilterBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class YttrREIPlugin implements REIPluginV0 {

	public static final Identifier ID = new Identifier("yttr", "main");
	public static final VoidFilteringCategory VOID_FILTERING = new VoidFilteringCategory();
	public static final PistonSmashingCategory PISTON_SMASHING = new PistonSmashingCategory();
	
	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategory(VOID_FILTERING);
		recipeHelper.registerCategory(PISTON_SMASHING);
	}
	
	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerWorkingStations(VoidFilteringCategory.ID, EntryStack.create(YItems.VOID_FILTER));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.PISTON));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.STICKY_PISTON));
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		for (VoidFilterBlockEntity.OutputEntry en : VoidFilterBlockEntity.OUTPUTS) {
			recipeHelper.registerDisplay(new VoidFilteringEntry(EntryStack.create(en.item), en.chance));
		}
		recipeHelper.registerDisplay(new PistonSmashingEntry(EntryStack.create(Blocks.SHROOMLIGHT), EntryStack.create(new ItemStack(YItems.GLOWING_GAS, 4)), EntryStack.create(YBlocks.YTTRIUM_BLOCK)));
		recipeHelper.registerDisplay(new PistonSmashingEntry(EntryStack.create(YBlocks.ULTRAPURE_CARBON_BLOCK), EntryStack.create(YItems.COMPRESSED_ULTRAPURE_CARBON), EntryStack.create(YBlocks.YTTRIUM_BLOCK), EntryStack.create(Blocks.DIAMOND_BLOCK)));
		recipeHelper.registerDisplay(new PistonSmashingEntry(EntryStack.create(YBlocks.COMPRESSED_ULTRAPURE_CARBON_BLOCK), EntryStack.create(YItems.ULTRAPURE_DIAMOND), EntryStack.create(Blocks.DIAMOND_BLOCK)));
	}
	
	@Override
	public Identifier getPluginIdentifier() {
		return ID;
	}

}
