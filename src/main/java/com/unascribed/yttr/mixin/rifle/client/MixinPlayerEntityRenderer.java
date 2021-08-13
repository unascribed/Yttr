package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.item.RifleItem;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.util.Hand;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

	@Inject(at=@At("HEAD"), method="getArmPose", cancellable=true)
	private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<ArmPose> ci) {
		if (player.getStackInHand(hand).getItem() instanceof RifleItem) {
			ci.setReturnValue(ArmPose.BOW_AND_ARROW);
		}
	}
	
}
