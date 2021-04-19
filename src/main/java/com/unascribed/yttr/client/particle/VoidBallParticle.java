package com.unascribed.yttr.client.particle;

import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.YttrClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class VoidBallParticle extends BillboardParticle {
	
	private static final Identifier TEXTURE = new Identifier("yttr", "textures/particle/void_ball.png");
	
	private static VertexBuffer buf1, buf2, buf3;
	
	public VoidBallParticle(ClientWorld world, double x, double y, double z, float r) {
		super(world, x, y, z);
		this.scale = r;
		maxAge = 40;
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ONE_MINUS_SRC_ALPHA);
		if (!MinecraftClient.isFancyGraphicsOrBetter()) {
			if (buf1 != null) {
				buf1.close();
				buf2.close();
				buf3.close();
				buf1 = buf2 = buf3 = null;
			}
			MinecraftClient mc = MinecraftClient.getInstance();
			mc.getTextureManager().bindTexture(TEXTURE);
			Tessellator.getInstance().getBuffer().begin(7, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
			setColorAlpha(age < 10 ? 1 : 1-((age-10)/(float)(maxAge-10)));
			super.buildGeometry(vertexConsumer, camera, tickDelta);
			Tessellator.getInstance().draw();
			return;
		}
		if (buf1 == null) {
			buf1 = new VertexBuffer(VertexFormats.POSITION);
			buf2 = new VertexBuffer(VertexFormats.POSITION);
			buf3 = new VertexBuffer(VertexFormats.POSITION);
			BufferBuilder bb = Tessellator.getInstance().getBuffer();
			
			final float PI = (float)Math.PI;
			int slices = 40;
			int stacks = 40;
			float radius = 1;
			
			// ported from LWJGL2 GLU Sphere https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/glu/Sphere.java
		
			float x, y, z;
			float rho, drho, theta, dtheta;
			int i, j, imin, imax;
			float nsign;

			nsign = 1.0f;

			drho = PI / stacks;
			dtheta = 2.0f * PI / slices;
			
			// draw +Z end as a triangle fan
			bb.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION);
			bb.vertex(0.0f, 0.0f, nsign * radius).next();
			for (j = 0; j <= slices; j++) {
				theta = (j == slices) ? 0.0f : j * dtheta;
				x = -MathHelper.sin(theta) * MathHelper.sin(drho);
				y = MathHelper.cos(theta) * MathHelper.sin(drho);
				z = nsign * MathHelper.cos(drho);
				bb.vertex(x * radius, y * radius, z * radius).next();
			}
			bb.end();
			buf1.upload(bb);
			
			imin = 1;
			imax = stacks - 1;

			// draw intermediate stacks as quad strips
			bb.begin(GL11.GL_QUAD_STRIP, VertexFormats.POSITION);
			for (i = imin; i < imax; i++) {
				rho = i * drho;
				for (j = 0; j <= slices; j++) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -MathHelper.sin(theta) * MathHelper.sin(rho);
					y = MathHelper.cos(theta) * MathHelper.sin(rho);
					z = nsign * MathHelper.cos(rho);
					bb.vertex(x * radius, y * radius, z * radius).next();
					x = -MathHelper.sin(theta) * MathHelper.sin(rho + drho);
					y = MathHelper.cos(theta) * MathHelper.sin(rho + drho);
					z = nsign * MathHelper.cos(rho + drho);
					bb.vertex(x * radius, y * radius, z * radius).next();
				}
			}
			bb.end();
			buf2.upload(bb);
			
			// draw -Z end as a triangle fan
			bb.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION);
			bb.vertex(0.0f, 0.0f, -radius * nsign).next();
			rho = PI - drho;
			for (j = slices; j >= 0; j--) {
				theta = (j == slices) ? 0.0f : j * dtheta;
				x = -MathHelper.sin(theta) * MathHelper.sin(rho);
				y = MathHelper.cos(theta) * MathHelper.sin(rho);
				z = nsign * MathHelper.cos(rho);
				bb.vertex(x * radius, y * radius, z * radius).next();
			}
			bb.end();
			buf3.upload(bb);
		}
		
		Vec3d cam = camera.getPos();
		float ox = (float)(MathHelper.lerp(tickDelta, prevPosX, x) - cam.getX());
		float oy = (float)(MathHelper.lerp(tickDelta, prevPosY, y) - cam.getY());
		float oz = (float)(MathHelper.lerp(tickDelta, prevPosZ, z) - cam.getZ());
		GlStateManager.pushMatrix();
		GlStateManager.color4f(0, 0, 0, age < 10 ? 1 : 1-((age-10)/(float)(maxAge-10)));
		GlStateManager.translatef(ox, oy, oz);
		GlStateManager.scalef(scale-0.5f, scale-0.5f, scale-0.5f);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();
		
		buf1.bind();
		VertexFormats.POSITION.startDrawing(0);
		YttrClient.drawBufferWithoutClobberingGLMatrix(buf1, null, GL11.GL_TRIANGLE_FAN);
		VertexFormats.POSITION.endDrawing();
		
		buf2.bind();
		VertexFormats.POSITION.startDrawing(0);
		YttrClient.drawBufferWithoutClobberingGLMatrix(buf2, null, GL11.GL_QUAD_STRIP);
		VertexFormats.POSITION.endDrawing();
		
		buf3.bind();
		VertexFormats.POSITION.startDrawing(0);
		YttrClient.drawBufferWithoutClobberingGLMatrix(buf3, null, GL11.GL_TRIANGLE_FAN);
		VertexFormats.POSITION.endDrawing();
		
		VertexBuffer.unbind();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
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
