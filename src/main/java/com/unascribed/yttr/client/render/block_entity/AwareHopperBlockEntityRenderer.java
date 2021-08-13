package com.unascribed.yttr.client.render.block_entity;

import com.unascribed.yttr.client.render.ZendermieModel;
import com.unascribed.yttr.content.block.abomination.AwareHopperBlockEntity;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AwareHopperBlockEntityRenderer extends BlockEntityRenderer<AwareHopperBlockEntity> {

	private static final Identifier ZENDERMIE_TEX = new Identifier("yttr", "textures/entity/zendermie.png");
	private static final Identifier ENDERMAN_TEX = new Identifier("minecraft", "textures/entity/enderman/enderman.png");
	private static final Identifier EYES_TEX = new Identifier("minecraft", "textures/entity/enderman/enderman_eyes.png");
	
	private final ZendermieModel zendermie = new ZendermieModel();
	
	public AwareHopperBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(AwareHopperBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.translate(0.5, 2.5, 0.5);
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
		zendermie.setAngles(null, entity.craftingTicks > 0 ? 1 : 0, 0, entity.age+tickDelta,
				MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw),
				MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadPitch, entity.headPitch));
		zendermie.arms.visible = true;
		if (entity.isBlind()) {
			zendermie.head.visible = false;
			matrices.push();
			zendermie.head.rotate(matrices);
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
			matrices.scale(0.6f, 0.6f, 0.6f);
			matrices.translate(-0.5f, -0.4f, -0.5f);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.CARVED_PUMPKIN.getDefaultState(), matrices, vertexConsumers, light, overlay);
			matrices.pop();
		} else {
			zendermie.head.visible = true;
		}
		zendermie.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(ZENDERMIE_TEX)), light, overlay, 1, 1, 1, 1);
		zendermie.arms.visible = false;
		zendermie.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(EYES_TEX)), LightmapTextureManager.pack(15, 15), overlay, 1, 1, 1, 1);
		matrices.pop();
	}
	
}
