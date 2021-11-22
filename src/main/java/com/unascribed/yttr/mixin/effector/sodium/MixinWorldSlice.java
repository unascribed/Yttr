package com.unascribed.yttr.mixin.effector.sodium;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(targets="me.jellysquid.mods.sodium.client.world.WorldSlice")
@Pseudo
public class MixinWorldSlice {
	
	// This is a very weird mixin because it applies to two different versions of Sodium's WorldSlice.

	private YttrWorld yttr$world;
	
	@Dynamic
	private int baseX;
	@Dynamic
	private int baseY;
	@Dynamic
	private int baseZ;
	
	@Inject(at=@At("TAIL"), method="init", require=0)
	public void init(@Coerce Object builder, World world, ChunkSectionPos chunkPos, WorldChunk[] chunks, CallbackInfo ci) {
		if (world instanceof YttrWorld) {
			yttr$world = (YttrWorld)world;
		}
	}
	
	@Inject(at=@At("TAIL"), method="<init>", require=0)
	public void init(World world, CallbackInfo ci) {
		if (world instanceof YttrWorld) {
			yttr$world = (YttrWorld)world;
		}
	}
	
	@Inject(at=@At("HEAD"), method="getBlockState(III)Lnet/minecraft/block/BlockState;", cancellable=true, require=0)
	public void getBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> ci) {
		if (yttr$world != null && yttr$world.yttr$isPhased(x, y, z)) {
			ci.setReturnValue(Blocks.VOID_AIR.getDefaultState());
		}
	}
	
	@Inject(at=@At("HEAD"), method="getBlockStateRelative(III)Lnet/minecraft/block/BlockState;", cancellable=true, require=0)
	public void getBlockStateRelative(int x, int y, int z, CallbackInfoReturnable<BlockState> ci) {
		if (yttr$world != null && yttr$world.yttr$isPhased(baseX+x, baseY+y, baseZ+z)) {
			ci.setReturnValue(Blocks.VOID_AIR.getDefaultState());
		}
	}
	
}
