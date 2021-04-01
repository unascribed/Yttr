package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.SqueezeSaplingGenerator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator {
	
	@Inject(at=@At("TAIL"), method="generateFeatures")
	public void generateFeatures(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci) {
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setPopulationSeed(region.getSeed(), region.getCenterChunkX(), region.getCenterChunkZ());
		if (chunkRandom.nextInt(40) == 0) {
			int x = (region.getCenterChunkX()*16)+chunkRandom.nextInt(16);
			int z = (region.getCenterChunkZ()*16)+chunkRandom.nextInt(16);
			chunkRandom.setPopulationSeed(region.getSeed(), region.getCenterChunkX()+x, region.getCenterChunkZ()+z);
			Biome b = region.getBiome(new BlockPos(x, 0, z));
			if (b.getCategory() == Category.OCEAN && b.getDepth() < -1.5f) {
				int y = region.getTopY(Type.OCEAN_FLOOR_WG, x, z);
				int waterSurface = region.getTopY(Type.WORLD_SURFACE_WG, x, z);
				if (waterSurface - y > 20) {
					new SqueezeSaplingGenerator().generate(region, new BlockPos(x, y, z), chunkRandom);
				}
			}
		}
	}
	
}
