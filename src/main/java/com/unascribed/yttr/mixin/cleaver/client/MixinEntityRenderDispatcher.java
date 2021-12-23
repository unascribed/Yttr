package com.unascribed.yttr.mixin.cleaver.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.SlopeStander;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/render/entity/EntityRenderer.render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
			method="render")
	public void render(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		double yO = MathHelper.lerp(tickDelta, ((SlopeStander)entity).yttr$getLastYOffset(), ((SlopeStander)entity).yttr$getYOffset());
		matrices.translate(0, yO, 0);
	}
	
}
