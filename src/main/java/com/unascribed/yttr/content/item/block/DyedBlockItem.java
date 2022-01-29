package com.unascribed.yttr.content.item.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class DyedBlockItem extends BlockItem implements ItemColorProvider {
	
	public final DyeColor color;

	public DyedBlockItem(Block block, DyeColor color, Settings settings) {
		super(block, settings);
		this.color = color;
	}

	@Override
	public String getTranslationKey() {
		return getOrCreateTranslationKey().replace("item", "block");
	}
	
	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		return color.getFireworkColor();
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			stacks.add(new ItemStack(this));
		}
	}

}
