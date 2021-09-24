package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

@Mixin(Item.class)
public interface AccessorItem {

	@Accessor("group")
	void yttr$setGroup(ItemGroup group);
	
}
