package com.unascribed.yttr.client;

import com.unascribed.yttr.PowerMeterBlock;
import com.unascribed.yttr.PowerMeterBlockEntity;
import com.unascribed.yttr.Yttr;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class PowerMeterBlockEntityRenderer extends BlockEntityRenderer<PowerMeterBlockEntity> {

	private static final Identifier LCD = new Identifier("yttr", "textures/block/lcd.png");
	
	public PowerMeterBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(PowerMeterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		long time = System.currentTimeMillis()-entity.readoutTime;
		if (time < 5200) {
			BlockState bs = entity.getCachedState();
			if (bs.getBlock() != Yttr.POWER_METER) return;
			float a = 1;
			if (time < 200) {
				a = time/200f;
			} else if (time > 5000) {
				a = (5200-time)/500f;
			}
			String readout = Integer.toString(entity.readout);
			VertexConsumer vc = vertexConsumers.getBuffer(a < 1 ? RenderLayer.getEntityTranslucent(LCD) : RenderLayer.getEntityCutout(LCD));
			matrices.push();
			matrices.translate(0.5f, 0.5f, 0.5f);
			float ang = 0;
			switch (bs.get(PowerMeterBlock.FACING)) {
				case NORTH:
				default:
					ang = 0;
					break;
				case WEST:
					ang = 90;
					break;
				case SOUTH:
					ang = 180;
					break;
				case EAST:
					ang = 270;
					break;
			}
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ang));
			matrices.translate(-0.5f, -0.5f, -0.5f);
			matrices.translate(1/16f, 0.5f, 0.89825f);
			for (int i = readout.length()-1; i >= 0; i--) {
				char c = readout.charAt(i);
				float u = Character.digit(c, 10)/10f;
				Matrix4f mat = matrices.peek().getModel();
				Matrix3f nrm = matrices.peek().getNormal();
				vc.vertex(mat, 0, 7/16f, 0).color(1f, 1f, 1f, a).texture(u+0.1f, 0).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 4/16f, 7/16f, 0).color(1f, 1f, 1f, a).texture(u, 0).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 4/16f, 0, 0).color(1f, 1f, 1f, a).texture(u, 1).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 0, 0, 0).color(1f, 1f, 1f, a).texture(u+0.1f, 1).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				matrices.translate(5/16f, 0, 0);
			}
			matrices.pop();
		}
	}

}
