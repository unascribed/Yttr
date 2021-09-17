package com.unascribed.yttr.world;

import java.util.Arrays;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorBiomeArray;

import net.minecraft.block.Blocks;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;

public class NetherRegenerator {

	private static final int HORIZONTAL_SECTION_COUNT = AccessorBiomeArray.yttr$getHorizontalSectionCount();
	private static final int VERTICAL_SECTION_COUNT = AccessorBiomeArray.yttr$getVerticalSectionCount();
	
	public static void onChunkLoad(ServerWorld world, WorldChunk chunk) {
		if (world.getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			BlockPos.Mutable bp = new BlockPos.Mutable(0, 0, 0);
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
				ChunkRandom rand = new ChunkRandom(world.getSeed());
				OctaveSimplexNoiseSampler noise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(1, 4, 8));
				OctaveSimplexNoiseSampler fireNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(1, 4, 8));
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
						if (height < 0) {
							for (int y = 128; y > 128+(height*8); y--) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
							}
						} else {
							if (height > 3) {
								height = 3+((height-3)*10);
							}
							for (int y = 128; y < 128+height; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
							}
							if (fireNoise.sample(bX/20D, bZ/20D, true) > 0.4) {
								BlockPos firePos = new BlockPos(x, (int)(128+height)+1, z);
								// postpone fire creation to prevent a deadlock in onBlockAdded
								world.getServer().send(new ServerTask(world.getServer().getTicks(), () -> {
									chunk.setBlockState(firePos, Blocks.FIRE.getDefaultState(), false);
								}));
							}
						}
					}
				}
				Biome[] data = ((AccessorBiomeArray)chunk.getBiomeArray()).yttr$getData();
				int y = MathHelper.clamp(128 >> 2, 0, BiomeArray.VERTICAL_BIT_MASK);
				int idx = y << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
				Arrays.fill(data, idx, data.length, world.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier("yttr", "scorched_summit")));
			}
		}
	}
	
}
