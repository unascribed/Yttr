package com.unascribed.yttr.init;

import java.util.function.Consumer;

import com.unascribed.yttr.Yttr;

import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.minecraft.client.sound.MusicType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class YBiomes {

	public static final Biome SCORCHED_SUMMIT = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.NETHER)
			.depth(0.1f)
			.scale(0.2f)
			.temperature(2)
			.downfall(0)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x3F76E4)
					.waterFogColor(0x50533)
					.fogColor(0x330808)
					.skyColor(0x220000)
					.music(MusicType.createIngameMusic(YSounds.DESERT_HEAT))
					.particleConfig(new BiomeParticleConfig(ParticleTypes.FLAME, 0.0015f))
					.build()
				)
			.spawnSettings(DefaultBiomeCreator.createNetherWastes().getSpawnSettings())
			.generationSettings(new GenerationSettings.Builder()
					.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE)
					.build()
				)
			.build();
	
	public static final Biome SCORCHED_TERMINUS = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.NETHER)
			.depth(0.1f)
			.scale(0.2f)
			.temperature(2)
			.downfall(0)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x3F76E4)
					.waterFogColor(0x50533)
					.fogColor(0x000000)
					.skyColor(0x000000)
					.music(MusicType.createIngameMusic(YSounds.DESERT_HEAT))
					.particleConfig(new BiomeParticleConfig(ParticleTypes.FLAME, 0.003f))
					.build()
				)
			.spawnSettings(DefaultBiomeCreator.createNetherWastes().getSpawnSettings())
			.generationSettings(new GenerationSettings.Builder()
					.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE)
					.build()
				)
			.build();
	
	public static final Biome WASTELAND = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.DESERT)
			.depth(0.05f)
			.scale(0.015f)
			.temperature(1.2f)
			.downfall(0.5f)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x403E16)
					.waterFogColor(0x403E16)
					.fogColor(0x6A7053)
					.skyColor(0x848970)
					.grassColor(0x58503F)
					.foliageColor(0x58503F)
					.moodSound(BiomeMoodSound.CAVE)
					.music(new MusicSound(YSounds.MANUSCRIPT, 3000, 6000, true))
					.build())
			.spawnSettings(modify(new SpawnSettings.Builder(),
						DefaultBiomeFeatures::addCaveMobs,
						b -> DefaultBiomeFeatures.addMonsters(b, 0, 40, 120))
					.build())
			.generationSettings(modify(new GenerationSettings.Builder(),
						DefaultBiomeFeatures::addLandCarvers,
						DefaultBiomeFeatures::addDefaultDisks,
						DefaultBiomeFeatures::addDefaultLakes,
						DefaultBiomeFeatures::addDefaultOres)
					.surfaceBuilder(YWorldGen.WASTELAND_SURFACE)
					.feature(Feature.VEGETAL_DECORATION, YWorldGen.WASTELAND_GRASS)
					.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIRT)
					.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRAVEL)
					.build()
				)
			.build();
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.BIOME, YBiomes.class, Biome.class);
		
		OverworldBiomes.addBiomeVariant(BiomeKeys.PLAINS, RegistryKey.of(Registry.BIOME_KEY, new Identifier("yttr", "wasteland")), 0.2);
	}

	@SafeVarargs
	private static <T> T modify(T obj, Consumer<T>... steps) {
		for (Consumer<T> step : steps) {
			step.accept(obj);
		}
		return obj;
	}
	
}
