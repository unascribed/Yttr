package com.unascribed.yttr.client.render;

import com.unascribed.yttr.block.decor.LampBlock;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.render.block_entity.LampBlockEntityRenderer;
import com.unascribed.yttr.item.block.LampBlockItem;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class LampItemRenderer extends IHasAClient {
	
	private static final LampBlockEntityRenderer lampItemGlow = new LampBlockEntityRenderer(null);
	
	public static void render(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BlockState state = ((BlockItem)stack.getItem()).getBlock().getDefaultState()
				.with(LampBlock.LIT, LampBlockItem.isInverted(stack))
				.with(LampBlock.COLOR, LampBlockItem.getColor(stack));
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
		matrices.translate(-0.5, -0.5, -0.5);
		BakedModel model = mc.getBlockRenderManager().getModel(state);
        int i = mc.getBlockColors().getColor(state, null, null, 0);
        float r = (i >> 16 & 255) / 255.0F;
        float g = (i >> 8 & 255) / 255.0F;
        float b = (i & 255) / 255.0F;
        mc.getBlockRenderManager().getModelRenderer().render(matrices.peek(),
        		vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), state, model, r, g, b, light, overlay);
        if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		lampItemGlow.render(mc.world, null, state, matrices, vertexConsumers, light, overlay);
	}
}
