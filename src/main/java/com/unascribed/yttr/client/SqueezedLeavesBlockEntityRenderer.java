package com.unascribed.yttr.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.yttr.SqueezedLeavesBlock;
import com.unascribed.yttr.SqueezedLeavesBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SqueezedLeavesBlockEntityRenderer extends BlockEntityRenderer<SqueezedLeavesBlockEntity> {

	public SqueezedLeavesBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(SqueezedLeavesBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.getCachedState().get(SqueezedLeavesBlock.SQUEEZING)) {
			// this is done in this weird way so the block entity doesn't have to be tickable
			long ticks = MinecraftClient.getInstance().world.getTime();
			if (entity.squeezeBegin == -1) {
				entity.squeezeBegin = ticks;
			}
			float time = ((ticks-entity.squeezeBegin)+tickDelta)%4;
			final float TAU = (float)(Math.PI*2);
			float a = 0.75f+((MathHelper.sin((time/4)*TAU)+1)/8);
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new Identifier("yttr", "block/squeeze_leaves"));
			float w = sprite.getMaxU()-sprite.getMinU();
			float h = sprite.getMaxV()-sprite.getMinV();
			GlStateManager.translatef(sprite.getMinU(), sprite.getMinV(), 0);
			GlStateManager.translatef(w/2, h/2, 0);
			GlStateManager.scalef(a, a, a);
			GlStateManager.translatef(-w/2, -h/2, 0);
			GlStateManager.translatef(-sprite.getMinU(), -sprite.getMinV(), 0);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			
			matrices.push();
			matrices.translate(0.5, 0.5, 0.5);
			matrices.scale(a, a, a);
			matrices.translate(-0.5, -0.5, -0.5);
			BlockState state = entity.getCachedState().with(SqueezedLeavesBlock.SQUEEZING, false);
			RenderLayer layer = RenderLayers.getBlockLayer(state);
			Immediate imm = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state,
					entity.getPos(), entity.getWorld(), matrices,
					imm.getBuffer(layer), true, entity.getWorld().random);
			imm.draw(layer);
			matrices.pop();
			
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		} else {
			entity.squeezeBegin = -1;
		}
	}

}
