package com.unascribed.yttr.mixin.effector.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
	
	@Inject(at=@At("TAIL"), method="renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
		if (stack.getItem() == YItems.EFFECTOR) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();
			float dmg = YItems.EFFECTOR.getFuel(stack);
			float maxDmg = EffectorItem.MAX_FUEL;
			int w = Math.round(dmg * 13 / maxDmg);
			renderGuiQuad(bb, x + 2, y + 13, 13, 1, 255, 255, 255, 255);
			renderGuiQuad(bb, x + 2, y + 13, w, 1, 0, 0, 0, 255);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}
	}
	
	@Shadow
	private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {}
	
}
