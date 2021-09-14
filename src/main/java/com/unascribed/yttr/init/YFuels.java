package com.unascribed.yttr.init;

import net.fabricmc.fabric.api.registry.FuelRegistry;

public class YFuels {

	public static void init() {
		FuelRegistry.INSTANCE.add(YItems.ULTRAPURE_CARBON, 1800);
		FuelRegistry.INSTANCE.add(YItems.ULTRAPURE_CARBON_BLOCK, 18000);
		FuelRegistry.INSTANCE.add(YItems.COMPRESSED_ULTRAPURE_CARBON, 20000);
		FuelRegistry.INSTANCE.add(YItems.COMPRESSED_ULTRAPURE_CARBON_BLOCK, 200000);
	}
	
}
