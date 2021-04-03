package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

@SuppressWarnings("deprecation")
public class YWorldGen {

	public static final ConfiguredFeature<?, ?> GADOLINITE_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					YBlocks.GADOLINITE.getDefaultState(),
					9))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(
					20,
					0,
					96)))
			.spreadHorizontally()
			.repeat(8);
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_FEATURE, YWorldGen.class, ConfiguredFeature.class);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("gadolinite_overworld"));
	}

	private static RegistryKey<ConfiguredFeature<?, ?>> key(String path) {
		return RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("yttr", path));
	}

}
