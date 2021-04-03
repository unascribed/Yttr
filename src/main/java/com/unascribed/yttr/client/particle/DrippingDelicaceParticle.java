package com.unascribed.yttr.client.particle;

import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public class DrippingDelicaceParticle extends BlockLeakParticle {

	public DrippingDelicaceParticle(ClientWorld clientWorld, double x, double y, double z) {
		super(clientWorld, x, y, z, Fluids.EMPTY);
		gravityStrength *= 0.01f;
		maxAge = 100;
		setColor(0.998f, 0.564f, 0.994f);
		setColorAlpha(0.5f);
		SpriteProvider sprites = ((ParticleManagerAccessor)MinecraftClient.getInstance().particleManager).getSpriteAwareFactories().get(Registry.PARTICLE_TYPE.getKey(ParticleTypes.DRIPPING_HONEY).get().getValue());
		setSprite(sprites);
	}

	@Override
	protected void updateAge() {
		if (maxAge-- <= 0) {
			markDead();
			FallingDelicaceParticle p = new FallingDelicaceParticle(world, x, y, z);
			p.setVelocity(velocityX, velocityY, velocityZ);
			MinecraftClient.getInstance().particleManager.addParticle(p);
		}
	}

	@Override
	protected void updateVelocity() {
		velocityX *= 0.02;
		velocityY *= 0.02;
		velocityZ *= 0.02;
	}
	
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

}
