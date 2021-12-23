package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.network.MessageC2SAttack;
import com.unascribed.yttr.util.Attackable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	
	@Inject(at=@At("HEAD"), method="doAttack")
	private void doAttack(CallbackInfo ci) {
		if (player != null && player.getMainHandStack().getItem() instanceof Attackable) {
			new MessageC2SAttack().sendToServer();
		}
	}
}
