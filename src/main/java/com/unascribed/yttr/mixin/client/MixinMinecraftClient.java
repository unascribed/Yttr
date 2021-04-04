package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Attackable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	
	@Inject(at=@At("HEAD"), method="doAttack")
	private void doAttack(CallbackInfo ci) {
		if (player != null && player.getMainHandStack().getItem() instanceof Attackable) {
			player.networkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(new Identifier("yttr", "attack"), new PacketByteBuf(Unpooled.buffer())));
		}
	}
}
