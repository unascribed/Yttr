package com.unascribed.yttr.mixin.neodymium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.Magnetized;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRenderer {

	private ItemEntity yttr$entity;
	
	@Inject(at=@At("HEAD"), method="render")
	public void capture(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		yttr$entity = itemEntity;
	}
	
	@Inject(at=@At("TAIL"), method="render")
	public void uncapture(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		yttr$entity = null;
	}
	
	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/client/render/model/BakedModel.getTransformation()Lnet/minecraft/client/render/model/json/ModelTransformation;"),
			method="render", ordinal=3)
	public float modifyBob(float f) {
		if (yttr$entity instanceof Magnetized) {
			Magnetized m = (Magnetized)yttr$entity;
			if (m.yttr$isMagnetizedBelow()) {
				if (m.yttr$isMagnetizedAbove()) {
					return 0f;
				}
				return -0.1f;
			} else if (m.yttr$isMagnetizedAbove()) {
				return -0.15f;
			}
		}
		return f;
	}
	
	
}
