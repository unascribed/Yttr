package com.unascribed.yttr.mixin.diving.client;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {
	
	@Shadow @Final
	private Text message;
	
	@Inject(at=@At(value="INVOKE", target="com/mojang/blaze3d/systems/RenderSystem.pushMatrix()V"), method="render")
	public void render(CallbackInfo ci) {
		if (message instanceof TranslatableText && "death.attack.yttr.suit_integrity_failure".equals(((TranslatableText)message).getKey())) {
			RenderSystem.clearColor(0, 0, 0, 1);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
		}
	}

}
