package com.unascribed.yttr.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.DelegatingVertexConsumer;
import com.unascribed.yttr.LampBlock;
import com.unascribed.yttr.LampBlockEntity;
import com.unascribed.yttr.WallLampBlock;
import com.unascribed.yttr.YttrClient;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LampBlockEntityRenderer extends BlockEntityRenderer<LampBlockEntity> {

	private static final RenderPhase.Transparency ADDITIVE_WITH_ALPHA = new RenderPhase.Transparency("yttr_additive_transparency_with_alpha", () -> {
	      RenderSystem.enableBlend();
	      RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
	   }, () -> {
	      RenderSystem.disableBlend();
	      RenderSystem.defaultBlendFunc();
	   });
	private static final RenderLayer HALO_LAYER = RenderLayer.of("yttr_lamp_halo", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true))
			.transparency(ADDITIVE_WITH_ALPHA)
			.writeMaskState(new RenderPhase.WriteMaskState(true, false))
			.build(false));
	
	public LampBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	// TODO it'd be really cool if this could be baked into the chunk or somehow optimized a little
	// I don't like that these have to be BERs
	
	@Override
	public void render(LampBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		render(entity.getWorld(), entity.getPos(), entity.getCachedState(), matrices, vertexConsumers, light, overlay);
	}
	
	public void render(World world, @Nullable BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		MinecraftClient.getInstance().getProfiler().push("yttr:lamp");
		if (state.getBlock() instanceof LampBlock && state.get(LampBlock.LIT)) {
			VertexConsumer vc = vertexConsumers.getBuffer(HALO_LAYER);
			int color = state.get(LampBlock.COLOR).glowColor;
			if (color == 0) color = 0x222222;
			float r = ((color >> 16)&0xFF)/255f;
			float g = ((color >> 8)&0xFF)/255f;
			float b = (color&0xFF)/255f;
			int l = LightmapTextureManager.pack(15, 15);
			BakedModel base = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
			BakedModel bm;
			try {
				YttrClient.retrievingHalo = true;
				bm = base.getOverrides().apply(base, ItemStack.EMPTY, MinecraftClient.getInstance().world, MinecraftClient.getInstance().player);
			} finally {
				YttrClient.retrievingHalo = false;
			}
			if (bm == null) return;
			DelegatingVertexConsumer dvc = new DelegatingVertexConsumer(vc) {
				@Override
				public VertexConsumer normal(float x, float y, float z) {
					return super.normal(0, 1, 0);
				}
			};
			
			// I would prefer not to hardcode this, but I don't have a lot of choice with how the blockstates get baked
			
			matrices.push();
			
			if (state.getBlock() instanceof WallLampBlock) {
				Direction dir = state.get(WallLampBlock.FACING);
				int x = 0;
				int y = 0;
				switch (dir) {
					case DOWN: break;
					case WEST: x = 90; y = 90; break;
					case NORTH: x = 90; break;
					case SOUTH: x = 90; y = 180; break;
					case EAST: x = 90; y = 270; break;
					case UP: x = 180; break;
				}
				matrices.translate(0.5, 0.5, 0.5);
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(y));
				matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(x));
				matrices.translate(-0.5, -0.5, -0.5);
			}
			
			for (BakedQuad bq : bm.getQuads(state, null, world.random)) {
				dvc.quad(matrices.peek(), bq, r, g, b, l, overlay);
			}
			for (Direction dir : Direction.values()) {
				if (pos == null || Block.shouldDrawSide(state, MinecraftClient.getInstance().world, pos, dir)) {
					for (BakedQuad bq : bm.getQuads(state, dir, world.random)) {
						dvc.quad(matrices.peek(), bq, r, g, b, l, overlay);
					}
				}
			}
			
			matrices.pop();
		}
		MinecraftClient.getInstance().getProfiler().pop();
	}
	
}
