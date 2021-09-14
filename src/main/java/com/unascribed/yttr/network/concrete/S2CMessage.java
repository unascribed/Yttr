package com.unascribed.yttr.network.concrete;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class S2CMessage extends Message {

	public S2CMessage(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected final void handle(PlayerEntity player) {
		handle(MinecraftClient.getInstance(), (ClientPlayerEntity)player);
	}
	
	@Environment(EnvType.CLIENT)
	protected abstract void handle(MinecraftClient client, ClientPlayerEntity player);

}
