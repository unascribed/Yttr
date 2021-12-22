package com.unascribed.yttr.client.render;

import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class LampItemRenderer extends IHasAClient {
	
	public static void render(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		LampColor color = LampBlockItem.getColor(stack);
		boolean lit = LampBlockItem.isInverted(stack);
		BlockState state = ((BlockItem)stack.getItem()).getBlock().getDefaultState()
				.with(LampBlock.LIT, lit)
				.with(LampBlock.COLOR, color);
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
		matrices.translate(-0.5, -0.5, -0.5);
		BakedModel model = mc.getBlockRenderManager().getModel(state);
        int i = mc.getBlockColors().getColor(state, null, null, 0);
        float r = (i >> 16 & 255) / 255.0F;
        float g = (i >> 8 & 255) / 255.0F;
        float b = (i & 255) / 255.0F;
        mc.getBlockRenderManager().getModelRenderer().render(matrices.peek(),
        		vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), state, model, r, g, b, light, overlay);
        if (lit) {
	        if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
			LampRenderer.render(mc.world, matrices, vertexConsumers.getBuffer(YRenderLayers.getLampHalo()), state, color.glowColor, Direction.NORTH, null);
        }
	}
}
