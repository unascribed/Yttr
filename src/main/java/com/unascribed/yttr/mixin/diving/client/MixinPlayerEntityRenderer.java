package com.unascribed.yttr.mixin.diving.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {

	@Inject(at=@At("HEAD"), method={"renderLeftArm", "renderRightArm"}, cancellable=true)
	private void renderFirstPersonArm(MatrixStack ms, VertexConsumerProvider vcp, int light, AbstractClientPlayerEntity e, CallbackInfo ci) {
		if (e.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE) {
			ci.cancel();
		}
	}
	
}
