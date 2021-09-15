package com.unascribed.yttr.network;

import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2SRifleMode extends C2SMessage {

	public RifleMode mode;
	
	public MessageC2SRifleMode(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SRifleMode(RifleMode mode) {
		super(YNetwork.CONTEXT);
		this.mode = mode;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (player.getMainHandStack().getItem() instanceof RifleItem) {
			((RifleItem)player.getMainHandStack().getItem()).changeMode(player, mode);
		}
	}

}
