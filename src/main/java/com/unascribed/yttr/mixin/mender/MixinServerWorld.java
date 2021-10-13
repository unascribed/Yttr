package com.unascribed.yttr.mixin.mender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mechanics.BedrockMender;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ServerWorld.class)
public class MixinServerWorld {

	@Inject(at=@At("TAIL"), method="tickChunk")
	public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		BedrockMender.tickChunk(chunk, randomTickSpeed);
	}
	
}
