package com.unascribed.yttr.network.concrete;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class C2SMessage extends Message {

	public C2SMessage(NetworkContext ctx) {
		super(ctx);
	}
	
	@Override
	protected final void handle(PlayerEntity player) {
		handle((ServerPlayerEntity)player);
	}
	
	@Environment(EnvType.CLIENT)
	protected abstract void handle(ServerPlayerEntity player);

}
