package com.unascribed.yttr.mixin.particlefix;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.unascribed.yttr.client.YttrClient;

import com.google.common.collect.Iterators;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public class MixinParticleManager {
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="java/util/List.iterator()Ljava/util/Iterator;"), method="renderParticles")
	public Iterator<ParticleTextureSheet> filterIterator(Iterator<ParticleTextureSheet> orig) {
		if (YttrClient.onlyRenderNonOpaqueParticles) {
			return Iterators.filter(orig, pts -> pts != ParticleTextureSheet.PARTICLE_SHEET_OPAQUE);
		}
		if (YttrClient.onlyRenderOpaqueParticles) {
			return Iterators.filter(orig, pts -> pts == ParticleTextureSheet.PARTICLE_SHEET_OPAQUE);
		}
		return orig;
	}
	
}
