package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;

@Mixin(Block.class)
public interface AccessorBlock {

	@Accessor("translationKey")
	void yttr$setTranslationKey(String key);
	
}
