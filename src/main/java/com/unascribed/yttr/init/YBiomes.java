package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.client.sound.MusicType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
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
			.spawnSettings(new SpawnSettings.Builder()
					.creatureSpawnProbability(0)
					.build()
				)
			.generationSettings(new GenerationSettings.Builder()
					.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE)
					.build()
				)
			.build();
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.BIOME, YBiomes.class, Biome.class);
	}
	
}
