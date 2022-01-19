package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

	private ItemStack yttr$capturedItem;
	private float yttr$equipProgress;
	
	@Inject(at=@At("HEAD"), method="renderFirstPersonItem")
	private void capture(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		yttr$capturedItem = item;
		yttr$equipProgress = equipProgress;
	}
	
	@Inject(at=@At("RETURN"), method="renderFirstPersonItem")
	private void uncapture(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		yttr$capturedItem = null;
		yttr$equipProgress = 0;
	}
	
	@Inject(at=@At("HEAD"), method="applySwingOffset", cancellable=true)
	private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress, CallbackInfo ci) {
		if (yttr$capturedItem != null && swingProgress > 0 && yttr$capturedItem.getItem() == YItems.SPATULA) {
			matrices.pop();
			matrices.push();
			int i = arm == Arm.RIGHT ? 1 : -1;
			float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
			float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
			applyEquipOffset(matrices, arm, yttr$equipProgress*MathHelper.sqrt(swingProgress));
			matrices.translate(i*g*-0.5f, f*0.5f, 0);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i * (f * 80.0F)));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i * (g * -90.0F)));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i * (g * -60.0F)));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i * (g * 90.0F)));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i * (g * -90.0F)));
//			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i * g * -20.0F));
//			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(g * -80.0F));
//			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i * -45.0F));
			ci.cancel();
		}
	}
	
	@Shadow
	private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {}
	
}
