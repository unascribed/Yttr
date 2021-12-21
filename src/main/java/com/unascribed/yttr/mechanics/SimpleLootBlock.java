package com.unascribed.yttr.mechanics;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public interface SimpleLootBlock {

	ItemStack getLoot(BlockState state);
	
}
