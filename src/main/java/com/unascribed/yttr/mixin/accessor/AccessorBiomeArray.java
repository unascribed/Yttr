package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;

@Mixin(BiomeArray.class)
public interface AccessorBiomeArray {

	@Accessor("HORIZONTAL_SECTION_COUNT")
	static int yttr$getHorizontalSectionCount() { throw new AbstractMethodError(); }
	
	@Accessor("VERTICAL_SECTION_COUNT")
	static int yttr$getVerticalSectionCount() { throw new AbstractMethodError(); }
	
	@Accessor("data")
	Biome[] yttr$getData();
	
}
