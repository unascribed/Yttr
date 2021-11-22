package com.unascribed.yttr.mixin.effector;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {

	@Shadow @Final
	private World world;
	@Shadow @Final
	private ChunkPos pos;
	
	@Inject(at=@At("HEAD"), method="getBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk$CreationType;)Lnet/minecraft/block/entity/BlockEntity;", cancellable=true)
	public void getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType, CallbackInfoReturnable<BlockEntity> ci) {
		if (world != null && world.isClient && world instanceof YttrWorld) {
			if (((YttrWorld)world).yttr$isPhased(this.pos, pos)) {
				ci.setReturnValue(null);
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="getBlockState", cancellable=true)
	public void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> ci) {
		if (world != null && world.isClient && world instanceof YttrWorld) {
			if (((YttrWorld)world).yttr$isPhased(this.pos, pos)) {
				ci.setReturnValue(Blocks.VOID_AIR.getDefaultState());
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="getFluidState", cancellable=true)
	public void getFluidState(BlockPos pos, CallbackInfoReturnable<FluidState> ci) {
		if (world != null && world.isClient && world instanceof YttrWorld) {
			if (((YttrWorld)world).yttr$isPhased(this.pos, pos)) {
				ci.setReturnValue(Fluids.EMPTY.getDefaultState());
			}
		}
	}
	
}
