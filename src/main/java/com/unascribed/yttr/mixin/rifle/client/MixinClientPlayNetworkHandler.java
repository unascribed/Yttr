package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.init.YSounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

	@Shadow
	private ClientWorld world;
	
	@Inject(at=@At("HEAD"), method="onPlaySoundFromEntity", cancellable=true)
	public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket pkt, CallbackInfo ci) {
		if (pkt.getSound() == YSounds.RIFLE_CHARGE_CANCEL) {
			SoundInstance si = YttrClient.rifleChargeSounds.remove(world.getEntityById(pkt.getEntityId()));
			if (si != null) {
				MinecraftClient.getInstance().send(() -> {
					MinecraftClient.getInstance().getSoundManager().stop(si);
				});
			}
			ci.cancel();
		} else if (pkt.getSound() == YSounds.DROP_CAST_CANCEL) {
			SoundInstance si = YttrClient.dropCastSounds.remove(world.getEntityById(pkt.getEntityId()));
			if (si != null) {
				MinecraftClient.getInstance().send(() -> {
					MinecraftClient.getInstance().getSoundManager().stop(si);
				});
			}
			ci.cancel();
		}
		// vanilla playSoundFromEntity ignores pitch, so we do it ourselves
		if (pkt.getSound().getId().getNamespace().equals("yttr")) {
			MinecraftClient.getInstance().send(() -> {
				MinecraftClient.getInstance().getSoundManager().play(new EntityTrackingSoundInstance(pkt.getSound(), pkt.getCategory(), pkt.getVolume(), pkt.getPitch(), world.getEntityById(pkt.getEntityId())));
			});
			ci.cancel();
		}
	}
	
}
