package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.DyeColor;

@Mixin(DyeColor.class)
public interface AccessorDyeColor {

	@Accessor("color")
	int yttr$getColor();
	
}
