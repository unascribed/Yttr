package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity {

	@Accessor("cachedState")
	void yttr$setCachedState(BlockState v);
	
}
