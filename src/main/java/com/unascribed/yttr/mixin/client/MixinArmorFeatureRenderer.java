package com.unascribed.yttr.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.init.YItems;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Mixin(ArmorFeatureRenderer.class)
public class MixinArmorFeatureRenderer {

	private ArmorItem yttr$armorItem;
	private boolean yttr$secondLayer;
	private String yttr$model;
	
	@Inject(at=@At("HEAD"), method="renderArmorParts")
	public void captureArgs(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, BipedEntityModel bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string, CallbackInfo ci) {
		yttr$armorItem = armorItem;
		yttr$secondLayer = bl2;
		yttr$model = string;
	}
	
	@Inject(at=@At("RETURN"), method="renderArmorParts")
	public void forgetArgs(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, BipedEntityModel bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string, CallbackInfo ci) {
		yttr$armorItem = null;
		yttr$secondLayer = false;
		yttr$model = null;
	}
	
	@ModifyArg(at=@At(value="INVOKE", target="net/minecraft/client/render/item/ItemRenderer.getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"),
			method="renderArmorParts", index=1)
	public RenderLayer modifyRenderLayer(RenderLayer orig) {
		if (storedEntity == null || storedSlot == null) return orig;
		ItemStack stack = storedEntity.getEquippedStack(storedSlot);
		if (stack.getItem() == YItems.SUIT_HELMET ||
				((stack.hasTag() && stack.getTag().getInt("yttr:DurabilityBonus") > 0) && (
					stack.getItem() == Items.DIAMOND_HELMET ||
					stack.getItem() == Items.DIAMOND_CHESTPLATE ||
					stack.getItem() == Items.DIAMOND_LEGGINGS ||
					stack.getItem() == Items.DIAMOND_BOOTS
				))) {
			return YRenderLayers.getArmorTranslucentNoCull(getArmorTexture(yttr$armorItem, yttr$secondLayer, yttr$model));
		}
		return orig;
	}
	
	@Shadow
	private Identifier getArmorTexture(ArmorItem armorItem, boolean bl, @Nullable String string) { throw new AbstractMethodError(); }
	
	// Below taken from Fabric API
	
	@Unique
	private LivingEntity storedEntity;
	@Unique
	private EquipmentSlot storedSlot;

	@Inject(method = "render", at = @At("HEAD"))
	private void storeEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We store the living entity wearing the armor before we render
		this.storedEntity = livingEntity;
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void storeSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity livingEntity, EquipmentSlot slot, int i, BipedEntityModel bipedEntityModel, CallbackInfo ci) {
		// We store the current armor slot that is rendering before we render each armor piece
		this.storedSlot = slot;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void removeStored(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We remove the stored data after we render
		this.storedEntity = null;
		this.storedSlot = null;
	}
	
}
