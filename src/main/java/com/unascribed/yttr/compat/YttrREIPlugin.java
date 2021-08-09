package com.unascribed.yttr.compat;

import java.util.Arrays;
import java.util.List;

import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeTypes;

import com.google.common.collect.Lists;

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
	public static final CentrifugingCategory CENTRIFUGING = new CentrifugingCategory();
	
	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategory(VOID_FILTERING);
		recipeHelper.registerCategory(PISTON_SMASHING);
		recipeHelper.registerCategory(CENTRIFUGING);
	}
	
	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerWorkingStations(VoidFilteringCategory.ID, EntryStack.create(YItems.VOID_FILTER));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.PISTON));
		recipeHelper.registerWorkingStations(PistonSmashingCategory.ID, EntryStack.create(Blocks.STICKY_PISTON));
		recipeHelper.registerWorkingStations(CentrifugingCategory.ID, EntryStack.create(YBlocks.CENTRIFUGE));
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		List<VoidFilteringRecipe> sorted = Lists.newArrayList(recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.VOID_FILTERING));
		sorted.sort((a, b) -> Double.compare(b.getChance(), a.getChance()));
		for (VoidFilteringRecipe r : sorted) {
			recipeHelper.registerDisplay(new VoidFilteringEntry(r.getId(), EntryStack.create(r.getOutput()), r.getChance()));
		}
		for (PistonSmashingRecipe r : recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.PISTON_SMASHING)) {
			ItemStack multCloudOutput = r.getCloudOutput().copy();
			multCloudOutput.setCount(multCloudOutput.getCount()*r.getCloudSize());
			recipeHelper.registerDisplay(new PistonSmashingEntry(r.getId(), r.getInput().getMatchingBlocks(), r.getCatalyst().getMatchingBlocks(), EntryStack.create(r.getOutput()),
					r.getCloudColor(), EntryStack.create(multCloudOutput)));
		}
		for (CentrifugingRecipe r : recipeHelper.getRecipeManager().listAllOfType(YRecipeTypes.CENTRIFUGING)) {
			List<ItemStack> inputs = Lists.newArrayList(Lists.transform(Arrays.asList(r.getInput().getMatchingStacksClient()), (is) -> {
				is = is.copy();
				is.setCount(r.getInputCount());
				return is;
			}));
			recipeHelper.registerDisplay(new CentrifugingEntry(r.getId(), EntryStack.ofItemStacks(inputs), EntryStack.ofItemStacks(r.getOutputs())));
		}
	}
	
	@Override
	public Identifier getPluginIdentifier() {
		return ID;
	}

}
