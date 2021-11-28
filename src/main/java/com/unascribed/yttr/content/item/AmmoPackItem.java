package com.unascribed.yttr.content.item;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class AmmoPackItem extends TrinketItem {

	public AmmoPackItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.BACKPACK);
	}
	
	@Environment(EnvType.CLIENT)
	private static final ModelIdentifier MODEL = new ModelIdentifier("yttr:ammo_pack_model#inventory");
	
	@Environment(EnvType.CLIENT)
	@Override
	public void render(String slot, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light,
			PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
		BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(MODEL);
		matrices.push();
			model.body.rotate(matrices);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
			matrices.translate(-8/16f, -12/16f, 2/16f);
			VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutout(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
			for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
				vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
			}
			for (Direction d : Direction.values()) {
				int i = d.ordinal();
				if (i != 1) continue;
				for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), d, RANDOM)) {
					vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
				}
			}
		matrices.pop();
	}

}
