package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.client.sound.MusicType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
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
	
	public static final Biome SCORCHED_HEIGHTS = new Biome.Builder()
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
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.BIOME, YBiomes.class, Biome.class);
	}
	
}
