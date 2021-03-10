package com.unascribed.yttr.client;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

public class VoidBallParticle extends BillboardParticle {
	
	private static final Identifier TEXTURE = new Identifier("yttr", "textures/particle/void_ball.png");
	
	public VoidBallParticle(ClientWorld world, double x, double y, double z, float r) {
		super(world, x, y, z);
		this.scale = r;
		maxAge = 40;
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ONE_MINUS_SRC_ALPHA);
		Tessellator.getInstance().getBuffer().begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
		setColorAlpha(age < 10 ? 1 : 1-((age-10)/(float)(maxAge-10)));
		super.buildGeometry(vertexConsumer, camera, tickDelta);
		Tessellator.getInstance().draw();
	}

	@Override
	protected float getMinU() {
		return 0;
	}

	@Override
	protected float getMaxU() {
		return 1;
	}

	@Override
	protected float getMinV() {
		return 0;
	}

	@Override
	protected float getMaxV() {
		return 1;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}


}
