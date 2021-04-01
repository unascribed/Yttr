package com.unascribed.yttr.client;

import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public class FallingDelicaceParticle extends BlockLeakParticle {

	public FallingDelicaceParticle(ClientWorld clientWorld, double x, double y, double z) {
		super(clientWorld, x, y, z, Fluids.EMPTY);
		gravityStrength = 0.01f;
		maxAge = 100;
		setColor(0.998f, 0.564f, 0.994f);
		setColorAlpha(0.5f);
		SpriteProvider sprites = ((ParticleManagerAccessor)MinecraftClient.getInstance().particleManager).getSpriteAwareFactories().get(Registry.PARTICLE_TYPE.getKey(ParticleTypes.FALLING_HONEY).get().getValue());
		setSprite(sprites);
	}
	
	public void setVelocity(double x, double y, double z) {
		velocityX = x;
		velocityY = y;
		velocityZ = z;
	}

	
	@Override
	protected void updateVelocity() {
		if (this.onGround) {
			this.markDead();
		}
	}
	
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

}
