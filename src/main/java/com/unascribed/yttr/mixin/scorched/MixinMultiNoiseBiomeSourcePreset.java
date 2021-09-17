package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

@Mixin(MultiNoiseBiomeSource.Preset.class)
public class MixinMultiNoiseBiomeSourcePreset {

	@Inject(at=@At("RETURN"), method="method_31088(Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$Preset;Lnet/minecraft/util/registry/Registry;Ljava/lang/Long;)Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;")
	private static void presetSetup(MultiNoiseBiomeSource.Preset preset, Registry<Biome> registry, Long seed, CallbackInfoReturnable<MultiNoiseBiomeSource> ci) {
		((ScorchedEnablement)ci.getReturnValue()).yttr$setScorchedBiomes(registry.get(new Identifier("yttr", "scorched_summit")), registry.get(new Identifier("yttr", "scorched_terminus")));
	}
	
}
