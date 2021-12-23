package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public class MixinTitleScreen {

	@Shadow
	private String splashText;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/gui/screen/TitleScreen.drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"),
			method="render")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if ("Vertical!".equals(splashText)) {
			RenderSystem.rotatef(90, 0, 0, 1);
		}
	}
	
}
