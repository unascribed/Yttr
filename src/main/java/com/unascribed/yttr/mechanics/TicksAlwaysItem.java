package com.unascribed.yttr.mechanics;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TicksAlwaysItem {
	void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot);
	void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected);
}
