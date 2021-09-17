package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.world.ScorchedGenerator;

import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator {
	
	@Shadow @Final
	private long worldSeed;
	
	@Inject(at=@At("TAIL"), method="generateFeatures")
	public void generateFeatures(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci) {
		ScorchedGenerator.populate(worldSeed, region, accessor);
	}
	
}
