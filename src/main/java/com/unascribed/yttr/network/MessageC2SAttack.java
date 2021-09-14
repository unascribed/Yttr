package com.unascribed.yttr.network;

import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.util.Attackable;

import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2SAttack extends C2SMessage {

	public MessageC2SAttack(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SAttack() {
		super(YNetwork.CONTEXT);
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (player.getMainHandStack().getItem() instanceof Attackable) {
			((Attackable)player.getMainHandStack().getItem()).attack(player);
		}
	}

}
