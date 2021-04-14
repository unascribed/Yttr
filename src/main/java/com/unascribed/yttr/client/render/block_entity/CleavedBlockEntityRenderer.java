package com.unascribed.yttr.client.render.block_entity;

import com.unascribed.yttr.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.client.cache.CleavedBlockModels;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CleavedBlockEntityRenderer extends BlockEntityRenderer<CleavedBlockEntity> {

	public CleavedBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(CleavedBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BakedModel model = CleavedBlockModels.getModel(entity);
		BlockPos bestLighting = entity.getPos();
		int bestLightingVal = entity.getWorld().getLightLevel(entity.getPos());
		// TODO just fix lighting instead of using this neighbor lighting hack
		for (Direction d : Direction.values()) {
			BlockPos pos = entity.getPos().offset(d);
			int val = entity.getWorld().getLightLevel(pos);
			if (val > bestLightingVal) {
				bestLighting = pos;
				bestLightingVal = val;
			}
		}
		if (model != null) {
			MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
					entity.getWorld(), model,
					entity.getCachedState(), bestLighting,
					matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()),
					true, entity.getWorld().random, 0, overlay);
		}
//		GlStateManager.pushMatrix();
//		GlStateManager.multMatrix(matrices.peek().getModel());
//		GlStateManager.disableTexture();
//		GlStateManager.disableCull();
//		GlStateManager.enablePolygonOffset();
//		GlStateManager.polygonOffset(3, 3);
//		Random rand = new Random(entity.hashCode());
//		for (Polygon polygon : entity.getPolygons()) {
//			GL11.glBegin(GL11.GL_POLYGON);
//			GlStateManager.color4f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1);
//			for (DEdge edge : polygon) {
//				GL11.glVertex3d(edge.srcPoint().x, edge.srcPoint().y, edge.srcPoint().z);
//			}
//			GL11.glEnd();
//		}
//		GlStateManager.enableCull();
//		GlStateManager.enableTexture();
//		GlStateManager.popMatrix();
	}

}
