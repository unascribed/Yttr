package com.unascribed.yttr.world;

import java.util.Arrays;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorBiomeArray;

import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;

public class ScorchedGenerator {

	private static final int HORIZONTAL_SECTION_COUNT = AccessorBiomeArray.yttr$getHorizontalSectionCount();
	private static final int VERTICAL_SECTION_COUNT = AccessorBiomeArray.yttr$getVerticalSectionCount();
	
	public static void generate(long worldSeed, ChunkRegion region, StructureAccessor accessor) {
		if (region.toServerWorld().getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			BlockPos.Mutable bp = new BlockPos.Mutable(0, 0, 0);
			Chunk chunk = region.getChunk(region.getCenterChunkX(), region.getCenterChunkZ());
			if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						bp.set(x, 0, z);
						chunk.setBlockState(bp, Blocks.BARRIER.getDefaultState(), false);
						for (int y = 1; y < 4; y++) {
							bp.set(x, y, z);
							chunk.setBlockState(bp, YBlocks.CORE_LAVA.getDefaultState(), false);
						}
						bp.set(x, 4, z);
						chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
						bp.set(x, 5, z);
						if (chunk.getBlockState(bp).isOf(Blocks.NETHERRACK)) {
							chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
						}
					}
				}
			}
			bp.setY(127);
			if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				ChunkRandom rand = new ChunkRandom(worldSeed);
				OctaveSimplexNoiseSampler noise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(1, 4, 8));
				OctaveSimplexNoiseSampler fireNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 10));
				OctaveSimplexNoiseSampler heightsBNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 3, 6));
				OctaveSimplexNoiseSampler heightsTNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 4));
				rand.setPopulationSeed(worldSeed, chunk.getPos().getStartX(), chunk.getPos().getStartZ());
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 120; y < 128; y++) {
							bp.set(x, y, z);
							if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
							}
						}
						int bX = (chunk.getPos().getStartX()+x);
						int bZ = (chunk.getPos().getStartZ()+z);
						double height = (noise.sample(bX/200D, bZ/200D, true)+0.2)*6;
						int lastY = 128;
						if (height < 0) {
							for (int y = 128; y > 128+(height*8); y--) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
								lastY = y-1;
							}
						} else {
							if (height > 3) {
								height = 3+((height-3)*10);
							}
							for (int y = 128; y < 128+height; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
								lastY = y;
							}
						}
						if (rand.nextDouble()*2 < (fireNoise.sample(bX/20D, bZ/20D, true)-0.2)) {
							bp.set(bX, lastY, bZ);
							if (!region.getBlockState(bp).isAir()) {
								bp.set(bX, lastY+1, bZ);
								region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
							}
						}
						
						double bh = heightsBNoise.sample(bX/100D, bZ/100D, true)*12;
						if (bh > 0) {
							double th = heightsTNoise.sample(bX/200D, bZ/200D, true)*6;
							int lastYH = 0;
							for (int y = (int)(220-bh); y < 220+th; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
								lastYH = y;
							}
							if (rand.nextDouble() < (fireNoise.sample(bX/30D, bZ/30D, false)+0.2)) {
								bp.set(bX, lastYH, bZ);
								if (!region.getBlockState(bp).isAir()) {
									bp.set(bX, lastYH+1, bZ);
									region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
								}
							}
						}
					}
				}
				Biome[] data = ((AccessorBiomeArray)chunk.getBiomeArray()).yttr$getData();
				int summitIdx = (128 >> 2) << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
				int heightsIdx = (192 >> 2) << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
				Arrays.fill(data, summitIdx, heightsIdx, region.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier("yttr", "scorched_summit")));
				Arrays.fill(data, heightsIdx, data.length, region.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier("yttr", "scorched_heights")));
			}
		}
	}
	
}
