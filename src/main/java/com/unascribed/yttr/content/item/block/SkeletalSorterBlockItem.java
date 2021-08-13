package com.unascribed.yttr.content.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;

public class SkeletalSorterBlockItem extends BlockItem {

	public final Arm mainHand;
	
	public SkeletalSorterBlockItem(Block block, Arm mainHand, Settings settings) {
		super(block, settings);
		this.mainHand = mainHand;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (isIn(group)) {
			stacks.add(new ItemStack(this));
		}

	}
	
}
