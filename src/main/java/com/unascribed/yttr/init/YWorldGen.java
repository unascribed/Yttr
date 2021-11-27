package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.LatchReference;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

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

	public static final ConfiguredFeature<?, ?> BROOKITE_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					YBlocks.BROOKITE_ORE.getDefaultState(),
					5))
			.rangeOf(32)
			.spreadHorizontally()
			.repeat(4);
	
	public static final LatchReference<ConfiguredFeature<?, ?>> COPPER_OVERWORLD = YLatches.create();
	
	public static final ConfiguredFeature<?, ?> WASTELAND_GRASS = Feature.RANDOM_PATCH
			.configure(new RandomPatchFeatureConfig.Builder(
					new SimpleBlockStateProvider(YBlocks.WASTELAND_GRASS.getDefaultState()), SimpleBlockPlacer.INSTANCE)
				.tries(4)
				.build())
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(4);
	
	public static final ConfiguredSurfaceBuilder<?> WASTELAND_SURFACE = SurfaceBuilder.DEFAULT
			.withConfig(new TernarySurfaceConfig(YBlocks.WASTELAND_DIRT.getDefaultState(), YBlocks.WASTELAND_DIRT.getDefaultState(), Blocks.STONE.getDefaultState()));
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_FEATURE, YWorldGen.class, ConfiguredFeature.class);
		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, YWorldGen.class, ConfiguredSurfaceBuilder.class);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("gadolinite_overworld"));
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("brookite_overworld"));
	}

	public static RegistryKey<ConfiguredFeature<?, ?>> key(String path) {
		return RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("yttr", path));
	}

}
