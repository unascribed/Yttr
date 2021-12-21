package com.unascribed.yttr.content.item;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.conditional.YTrinkets;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class CuprosteelCoilItem extends TrinketItem {

	public CuprosteelCoilItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.FEET) && slot.equals(YTrinkets.SOLE);
	}
	
	@Override
	public int getEnchantability() {
		return 12;
	}
	
	private static final Identifier TEXTURE = new Identifier("yttr", "textures/entity/coil.png");
	
	@Environment(EnvType.CLIENT)
	@Override
	public void render(String slot, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light,
			PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
		if (player.getEquippedStack(EquipmentSlot.FEET).getItem() == YItems.SUIT_BOOTS) return;
		VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
		boolean hasBoots = Yttr.isVisuallyWearingBoots.test(player);
		renderCoil(vc, matrices, model.leftLeg, hasBoots, light, true);
		renderCoil(vc, matrices, model.rightLeg, hasBoots, light, false);
		ItemStack is = TrinketsApi.getTrinketComponent(player).getStack(slot);
		if (is.getItem() == this && is.hasGlint()) {
			vc = vertexConsumer.getBuffer(RenderLayer.getEntityGlint());
			renderCoil(vc, matrices, model.leftLeg, hasBoots, light, true);
			renderCoil(vc, matrices, model.rightLeg, hasBoots, light, false);
		}
	}

	@Environment(EnvType.CLIENT)
	private void renderCoil(VertexConsumer vc, MatrixStack matrices, ModelPart part, boolean hasBoots, int light, boolean flip) {
		matrices.push();
			part.rotate(matrices);
			matrices.translate(0, 12.1f/16f, 0);
			if (hasBoots) {
				matrices.translate(0.5f/16f*(flip?1:-1), 1/16f, 0);
			}
			matrices.scale(1.5f/16f, 1, 1.5f/16f);
			Matrix4f mmat = matrices.peek().getModel();
			Matrix3f nmat = matrices.peek().getNormal();
			vc.vertex(mmat, -1, 0,  1).color(1f, 1f, 1f, 1f).texture(flip ? 1 : 0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat,  1, 0,  1).color(1f, 1f, 1f, 1f).texture(flip ? 0 : 1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat,  1, 0, -1).color(1f, 1f, 1f, 1f).texture(flip ? 0 : 1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat, -1, 0, -1).color(1f, 1f, 1f, 1f).texture(flip ? 1 : 0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
		matrices.pop();
	}

}
