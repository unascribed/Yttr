package com.unascribed.yttr.mixin.effector.caffeine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.EffectorWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

@Mixin(targets="me.jellysquid.mods.sodium.client.world.WorldSlice", remap=false)
public abstract class MixinWorldSlice {

	@Shadow
	private World world;
	
	@Shadow
	private int minX;
	@Shadow
	private int maxX;
	
	@Shadow
	private int minY;
	@Shadow
	private int maxY;
	
	@Shadow
	private int minZ;
	@Shadow
	private int maxZ;
	
	@Inject(at=@At("TAIL"), method="populateBlockArrays", locals=LocalCapture.CAPTURE_FAILHARD)
	private void populateBlockArrays(int sectionIdx, ChunkSectionPos pos, Chunk chunk, CallbackInfo ci,
			ChunkSection section, PalettedContainer<BlockState> container, PackedIntegerArray intArray, Palette<BlockState> palette, BlockState[] dst) {
		if (world instanceof EffectorWorld) {
			EffectorWorld ew = (EffectorWorld)world;
			
			int minBlockX = Math.max(this.minX, pos.getMinX());
			int maxBlockX = Math.min(this.maxX, pos.getMaxX());
	
			int minBlockY = Math.max(this.minY, pos.getMinY());
			int maxBlockY = Math.min(this.maxY, pos.getMaxY());
	
			int minBlockZ = Math.max(this.minZ, pos.getMinZ());
			int maxBlockZ = Math.min(this.maxZ, pos.getMaxZ());
	
			BlockPos.Mutable mut = new BlockPos.Mutable();
			
			for (int y = minBlockY; y <= maxBlockY; y++) {
				for (int z = minBlockZ; z <= maxBlockZ; z++) {
					for (int x = minBlockX; x <= maxBlockX; x++) {
						if (ew.yttr$isPhased(mut.set(x, y, z))) {
							int blockIdx = getLocalBlockIndex(x & 15, y & 15, z & 15);
							dst[blockIdx] = Blocks.VOID_AIR.getDefaultState();
						}
					}
				}
			}
		}
	}
	
	@Shadow
	private static int getLocalBlockIndex(int x, int y, int z) { return -1; }
	
}
