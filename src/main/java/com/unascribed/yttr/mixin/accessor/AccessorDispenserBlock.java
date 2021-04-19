package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;

@Mixin(DispenserBlock.class)
public interface AccessorDispenserBlock {

	@Invoker("getBehaviorForItem")
	DispenserBehavior yttr$getBehaviorForItem(ItemStack stack);
	
}
