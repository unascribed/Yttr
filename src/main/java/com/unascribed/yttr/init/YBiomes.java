package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeCreator;

public class YBiomes {

	public static final Biome SCORCHED_SUMMIT = DefaultBiomeCreator.createNetherWastes();
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.BIOME, YBiomes.class, Biome.class);
	}
	
}
