package com.unascribed.yttr.client.render.block_entity;

import java.util.Random;

import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockEntityRenderer extends BlockEntityRenderer<CleavedBlockEntity> {

	public CleavedBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(CleavedBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (!MinecraftClient.getInstance().options.debugEnabled) return;
		Random rand = new Random(entity.hashCode());
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		VertexConsumer lines = vertexConsumers.getBuffer(RenderLayer.getLines());
		Matrix4f mat = matrices.peek().getModel();
		for (Polygon polygon : entity.getPolygons()) {
			float cX = 0;
			float cY = 0;
			float cZ = 0;
			for (DEdge de : polygon) {
				cX += de.srcPoint().x;
				cY += de.srcPoint().y;
				cZ += de.srcPoint().z;
				
				lines.vertex(mat, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z).color(r, g, b, 1).next();
				lines.vertex(mat, (float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z).color(r, g, b, 1).next();
			}
			cX /= polygon.nPoints();
			cY /= polygon.nPoints();
			cZ /= polygon.nPoints();
			Vec3d normal = polygon.plane().normal();
			float nX = (float)normal.x/2;
			float nY = (float)normal.y/2;
			float nZ = (float)normal.z/2;
			
			lines.vertex(mat, cX, cY, cZ).color(r, g, b, 1).next();
			lines.vertex(mat, cX+nX, cY+nY, cZ+nZ).color(r, g, b, 1).next();
		}
	}

}
