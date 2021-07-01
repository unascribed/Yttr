package com.unascribed.yttr.mixin.effector.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion {

	@Shadow @Final
	protected BlockState[] blockStates;
	@Shadow @Final
	protected FluidState[] fluidStates;
	
	@Inject(at=@At("TAIL"), method="<init>")
	public void construct(World world, int chunkX, int chunkZ, WorldChunk[][] chunks, BlockPos startPos, BlockPos endPos, CallbackInfo ci) {
		if (world instanceof YttrWorld) {
			YttrWorld ew = (YttrWorld)world;
			for (BlockPos pos : BlockPos.iterate(startPos, endPos)) {
				if (ew.yttr$isPhased(pos)) {
					int idx = getIndex(pos);
					blockStates[idx] = Blocks.VOID_AIR.getDefaultState();
					fluidStates[idx] = Fluids.EMPTY.getDefaultState();
				}
			}
		}
	}
	
	@Shadow
	protected abstract int getIndex(BlockPos pos);
	
}
