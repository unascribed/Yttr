package com.unascribed.yttr.mixin.diving.client;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public class MixinDeathScreen {
	
	@Shadow @Final
	private Text message;
	
	@Inject(at=@At(value="INVOKE", target="com/mojang/blaze3d/systems/RenderSystem.pushMatrix()V"), method="render")
	public void render(CallbackInfo ci) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && player.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE && player.getY() < 0) {
			RenderSystem.clearColor(0, 0, 0, 1);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
		}
	}

}
