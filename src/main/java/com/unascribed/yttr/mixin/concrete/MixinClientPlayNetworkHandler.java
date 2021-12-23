package com.unascribed.yttr.mixin.concrete;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.network.concrete.NetworkContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	
	@Inject(at=@At("HEAD"), method="onCustomPayload(Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V", cancellable=true)
	public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		for (NetworkContext ctx : NetworkContext.contexts) {
			if (ctx.handleCustomPacket((ClientPlayNetworkHandler)(Object)this, packet)) {
				ci.cancel();
				return;
			}
		}
	}
	
}
