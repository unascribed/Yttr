package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.YttrClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

	@Shadow
	private ClientWorld world;
	
	@Inject(at=@At("HEAD"), method="onPlaySoundFromEntity(Lnet/minecraft/network/packet/s2c/play/PlaySoundFromEntityS2CPacket;)V", cancellable=true)
	public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket pkt, CallbackInfo ci) {
		if (pkt.getSound() == Yttr.RIFLE_CHARGE_CANCEL) {
			SoundInstance si = YttrClient.rifleChargeSounds.remove(world.getEntityById(pkt.getEntityId()));
			if (si != null) {
				MinecraftClient.getInstance().getSoundManager().stop(si);
			}
			ci.cancel();
		}
	}
	
}
