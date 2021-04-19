package com.unascribed.yttr.client.render.block_entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.block.mechanism.ReplicatorBlockEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ReplicatorBlockEntityRenderer extends BlockEntityRenderer<ReplicatorBlockEntity> {

	public ReplicatorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(ReplicatorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		// See ReplicatorRenderer for the actual renderer
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			RenderSystem.disableLighting();
			matrices.push();
			matrices.translate(0.5, 0.5, 0.5);
			Camera cam = MinecraftClient.getInstance().gameRenderer.getCamera();
			matrices.multiply(cam.getRotation());
			matrices.scale(-0.0125F, -0.0125F, 0.01f);
			matrices.translate(0, -50, 0);

			int y = 0;
			String[] rant = {
					"§c§lDISABLE FABULOUS GRAPHICS",
					"Fabulous graphics are a hacky way to make Minecraft pretend it has good translucency support",
					"It was added only because a higher-up forced the Java Edition team to",
					"It has massively complicated and broken the rendering pipeline",
					"I refuse to bend over backwards to support this broken system",
					"§e§oPlease do not play modded with Fabulous graphics enabled, with or without Yttr!"
			};
			TextRenderer tr = MinecraftClient.getInstance().textRenderer;
			for (String s : rant) {
				tr.draw(s, -(tr.getWidth(s)/2), y, -1, false, matrices.peek().getModel(), vertexConsumers, false, 0xFF000000, light);
				y += 12;
			}
			
			matrices.pop();
		}
	}
	
	
}
