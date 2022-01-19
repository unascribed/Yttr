package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YttrClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderer;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Inject(at=@At("HEAD"), method="innerRenderInGui")
	private void innerRenderInGuiHead(CallbackInfo ci) {
		YttrClient.renderingGui = true;
	}
	
	@Inject(at=@At("RETURN"), method="innerRenderInGui")
	private void innerRenderInGuiReturn(CallbackInfo ci) {
		YttrClient.renderingGui = false;
	}
	
}
