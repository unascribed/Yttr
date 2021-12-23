package com.unascribed.yttr.mixin.continuity.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.ContinuityItemColorProvider;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Inject(at=@At("HEAD"), method="renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;II)V")
	public void renderItemHead(LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vcp, World w, int light, int overlay, CallbackInfo ci) {
		if (entity != null && stack.getItem() == YItems.SHIFTER) {
			if (mode.isFirstPerson()) return;
			ContinuityItemColorProvider.holderYaw = entity.bodyYaw;
			ContinuityItemColorProvider.holderYawValid = true;
		}
	}
	
	@Inject(at=@At("TAIL"), method="renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;II)V")
	public void renderItemTail(LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vcp, World w, int light, int overlay, CallbackInfo ci) {
		if (entity != null && stack.getItem() == YItems.SHIFTER) {
			ContinuityItemColorProvider.holderYawValid = false;
		}
	}
	
}
