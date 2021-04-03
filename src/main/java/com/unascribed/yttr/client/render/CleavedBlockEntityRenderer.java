package com.unascribed.yttr.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.yttr.block.entity.CleavedBlockEntity;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockEntityRenderer extends BlockEntityRenderer<CleavedBlockEntity> {

	public CleavedBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(CleavedBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glPointSize(8);
		GlStateManager.pushMatrix();
		GlStateManager.multMatrix(matrices.peek().getModel());
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		GL11.glBegin(GL11.GL_POINTS);
		for (ImmutableList<Vec3d> polygon : entity.getPolygons()) {
			for (Vec3d point : polygon) {
				GlStateManager.color4f((float)point.x, (float)point.y, (float)point.z, 1);
				GL11.glVertex3d(point.x, point.y, point.z);
			}
		}
		GL11.glEnd();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

}
