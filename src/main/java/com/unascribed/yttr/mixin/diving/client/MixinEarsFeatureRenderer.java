package com.unascribed.yttr.mixin.diving.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.item.SuitArmorItem;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;

@Mixin(targets="com.unascribed.ears.EarsFeatureRenderer")
@Pseudo
public class MixinEarsFeatureRenderer {

	private AbstractClientPlayerEntity yttr$capturedPlayer;
	
	@Shadow(remap=false)
	private int skipRendering;
	
	@Inject(at=@At("HEAD"), method="render")
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
		yttr$capturedPlayer = entity;
	}
	@Inject(at=@At("HEAD"), method="renderLeftArm")
	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, CallbackInfo ci) {
		yttr$capturedPlayer = entity;
	}
	@Inject(at=@At("HEAD"), method="renderRightArm")
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, CallbackInfo ci) {
		yttr$capturedPlayer = entity;
	}
	
	@Inject(at=@At("TAIL"), method={"render", "renderLeftArm", "renderRightArm"})
	public void discardCapture(CallbackInfo ci) {
		yttr$capturedPlayer = null;
	}
	
	@Inject(at=@At("HEAD"), method="anchorTo", cancellable=true, remap=false)
	public void anchorTo(@Coerce Enum<?> part, CallbackInfo ci) {
		if (yttr$capturedPlayer == null) return;
		String name = part.name();
		EquipmentSlot slot;
		switch (name) {
			case "HEAD": slot = EquipmentSlot.HEAD; break;
			case "TORSO": case "LEFT_ARM": case "RIGHT_ARM": slot = EquipmentSlot.CHEST; break;
			case "LEFT_LEG": case "RIGHT_LEG": slot = EquipmentSlot.LEGS; break;
			default: return;
		}
		if (yttr$capturedPlayer.getEquippedStack(slot).getItem() instanceof SuitArmorItem) {
			if (skipRendering == 0) skipRendering = 1;
			ci.cancel();
		}
	}
	
	
}
