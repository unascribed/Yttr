package com.unascribed.yttr;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YItems;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Substitutes {

	private static final BiMap<Item, Item> MAP = ImmutableBiMap.<Item, Item>builder()
			.put(YItems.ULTRAPURE_CARBON, Items.COAL)
			.put(YItems.ULTRAPURE_CINNABAR, Items.REDSTONE)
			.put(YItems.ULTRAPURE_GOLD, Items.GOLD_INGOT)
			.put(YItems.ULTRAPURE_IRON, Items.IRON_INGOT)
			.put(YItems.ULTRAPURE_LAZURITE, Items.LAPIS_LAZULI)
			.put(YItems.ULTRAPURE_SILICA, Items.QUARTZ)
			.put(YItems.ULTRAPURE_YTTRIUM, YItems.YTTRIUM_INGOT)
			.put(YItems.ULTRAPURE_NEODYMIUM, YItems.NEODYMIUM_DISC)
			.put(YItems.ULTRAPURE_DIAMOND, Items.DIAMOND)
			.build();
	
	public static Set<Item> allPrimes() {
		return MAP.inverse().keySet();
	}
	
	public static Set<Item> allSubstitutes() {
		return MAP.keySet();
	}
	
	public static @Nullable Item getPrime(Item substitute) {
		return MAP.get(substitute);
	}
	
	public static @Nullable Item getSubstitute(Item prime) {
		return MAP.inverse().get(prime);
	}
	
	public static ItemStack sub(ItemStack stack) {
		return copyWithAltItem(stack, getSubstitute(stack.getItem()));
	}

	public static ItemStack prime(ItemStack stack) {
		return copyWithAltItem(stack, getPrime(stack.getItem()));
	}
	
	private static ItemStack copyWithAltItem(ItemStack stack, Item item) {
		if (item == null) return stack.copy();
		ItemStack copy = new ItemStack(item);
		copy.setCount(stack.getCount());
		copy.setTag(stack.getTag());
		return copy;
	}
	
}
