package com.unascribed.yttr.mixin.continuity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.InteractionType;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow
	public ServerPlayerEntity player;
	
	@Inject(at=@At("HEAD"), method="onPlayerInteractEntity", cancellable=true)
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
		ServerWorld world = player.getServerWorld();
		Entity entity = packet.getEntity(world);
		if (entity != null && entity instanceof ItemEntity
				&& packet.getType() == InteractionType.ATTACK
				&& ((ItemEntity)entity).getStack().getItem() == YItems.DISC_OF_CONTINUITY) {
			// prevent the player getting kicked for attacking an invalid entity
			ci.cancel();
		}
	}
	
}
