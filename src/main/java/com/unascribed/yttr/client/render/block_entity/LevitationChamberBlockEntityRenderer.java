package com.unascribed.yttr.client.render.block_entity;

import com.unascribed.yttr.content.block.mechanism.LevitationChamberBlock;
import com.unascribed.yttr.content.block.mechanism.LevitationChamberBlockEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Vec3d;

public class LevitationChamberBlockEntityRenderer extends BlockEntityRenderer<LevitationChamberBlockEntity> {

	private final ShulkerBulletEntityRenderer bullet;
	private final ShulkerBulletEntity dummy = new ShulkerBulletEntity(EntityType.SHULKER_BULLET, null) {
		@Override
		public boolean shouldRenderName() {
			return false;
		}
	};
	
	public LevitationChamberBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
		bullet = new ShulkerBulletEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
	}

	@Override
	public void render(LevitationChamberBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Vec3d eyes = MinecraftClient.getInstance().getCameraEntity().getCameraPosVec(1);
		switch (entity.getCachedState().get(LevitationChamberBlock.OBSTRUCTION)) {
			case CHUTE:
				if (eyes.y < entity.getPos().getY()+1) return;
				if (eyes.squaredDistanceTo(entity.getPos().getX()+0.5, eyes.y, entity.getPos().getZ()+0.5) > 2*2) return;
				break;
			case NONE:
				if (eyes.y < entity.getPos().getY()+1.1) return;
				break;
			case SOLID:
				return;
		}
		matrices.push();
		matrices.translate(0.5, 0.25, 0.5);
		dummy.age = entity.age;
		try {
			bullet.render(dummy, 0, tickDelta, matrices, vertexConsumers, light);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		matrices.pop();
	}
	
}
