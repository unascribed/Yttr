package com.unascribed.yttr.mechanics;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.unascribed.yttr.crafting.SoakingRecipe;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.mixinsupport.WetWorld;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class SoakingHandler {

	public static void startServerWorldTick(ServerWorld world) {
		((WetWorld)world).yttr$getSoakingMap().clear();
	}
	
	public static void endServerWorldTick(ServerWorld world) {
		Multimap<BlockPos, ItemEntity> soakingMap = ((WetWorld)world).yttr$getSoakingMap();
		Table<BlockPos, Fluid, Integer> timeTable = ((WetWorld)world).yttr$getTimeTable();
		Iterator<Table.Cell<BlockPos, Fluid, Integer>> iter = timeTable.cellSet().iterator();
		while (iter.hasNext()) {
			Table.Cell<BlockPos, Fluid, Integer> cell = iter.next();
			if (!soakingMap.containsKey(cell.getRowKey())
					|| !world.isChunkLoaded(cell.getRowKey())
					|| world.getFluidState(cell.getRowKey()).getFluid() != cell.getColumnKey()) {
				iter.remove();
			}
		}
		for (Map.Entry<BlockPos, Collection<ItemEntity>> en : soakingMap.asMap().entrySet()) {
			FluidState fs = world.getFluidState(en.getKey());
			if (fs.isEmpty()) continue;
			Fluid f = fs.getFluid();
			Set<ItemEntity> unmatched = Sets.newHashSet(en.getValue());
			while (!unmatched.isEmpty()) {
				SoakingRecipe recipe = null;
				Set<ItemEntity> matched = Sets.newHashSet();
				for (SoakingRecipe sr : world.getServer().getRecipeManager().listAllOfType(YRecipeTypes.SOAKING)) {
					if (sr.getCatalyst().test(f)) {
						boolean matchedAll = true;
						Set<ItemEntity> maybeMatched = Sets.newHashSet();
						for (Ingredient i : sr.getIngredients()) {
							boolean ingredientMatched = false;
							Iterator<ItemEntity> unmIter = unmatched.iterator();
							while (unmIter.hasNext()) {
								ItemEntity ie = unmIter.next();
								if (!ie.getStack().isEmpty() && i.test(ie.getStack())) {
									maybeMatched.add(ie);
									ingredientMatched = true;
									break;
								}
							}
							if (!ingredientMatched) {
								matchedAll = false;
								break;
							}
						}
						if (matchedAll) {
							recipe = sr;
							matched.addAll(maybeMatched);
							unmatched.removeAll(maybeMatched);
							break;
						}
					}
				}
				if (recipe != null) {
					if (recipe.getTime() != 0) {
						if (timeTable.row(en.getKey()).compute(f, (_f, i) -> i == null ? 0 : i+1) < recipe.getTime()) {
							continue;
						}
						timeTable.put(en.getKey(), f, recipe.getTime()-recipe.getMultiDelay());
					}
					int toCraft = 1;
					if (recipe.getMultiDelay() == 0 && recipe.getResult().left().isPresent()) {
						toCraft = 64;
						for (ItemEntity ie : matched) {
							toCraft = Math.min(ie.getStack().getCount(), toCraft);
						}
					}
					final int toCraftf = toCraft;
					for (ItemEntity ie : matched) {
						ItemStack is = ie.getStack();
						is.decrement(toCraft);
						ie.setStack(is);
						if (is.isEmpty()) {
							ie.remove();
						}
					}
					recipe.getResult()
						.ifLeft(is -> {
							is = is.copy();
							is.setCount(toCraftf);
							Block.dropStack(world, en.getKey(), is);
						})
						.ifRight(bs -> world.setBlockState(en.getKey(), bs));
					if (recipe.getSound() != null) {
						world.playSound(null, en.getKey(), recipe.getSound(), SoundCategory.BLOCKS, 1, 1);
					}
				} else {
					// we're out of matching recipes
					break;
				}
			}
		}
	}
	
}
