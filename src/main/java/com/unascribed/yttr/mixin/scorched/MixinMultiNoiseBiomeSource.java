package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinMultiNoiseBiomeSource implements ScorchedEnablement {

	private Biome yttr$scorchedSummit = null;
	private Biome yttr$scorchedTerminus = null;
	
	@Inject(at=@At("HEAD"), method="getBiomeForNoiseGen(III)Lnet/minecraft/world/biome/Biome;", cancellable=true)
	public void getBiomeForNoiseGen(int bX, int bY, int bZ, CallbackInfoReturnable<Biome> ci) {
		if (yttr$scorchedSummit != null) {
			if (bY > (192>>2) && yttr$scorchedTerminus != null) {
				ci.setReturnValue(yttr$scorchedTerminus);
			} else if (bY > (128>>2)) {
				ci.setReturnValue(yttr$scorchedSummit);
			}
		}
	}
	
	@Override
	public void yttr$setScorchedBiomes(Biome summit, Biome terminus) {
		yttr$scorchedSummit = summit;
		yttr$scorchedTerminus = terminus;
	}

}
