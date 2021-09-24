package com.unascribed.yttr.network;

import com.unascribed.yttr.content.item.ShifterItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2SShifterMode extends C2SMessage {

	public boolean disconnected, hidden, plane;
	
	public MessageC2SShifterMode(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SShifterMode(boolean disconnected, boolean hidden, boolean plane) {
		super(YNetwork.CONTEXT);
		this.disconnected = disconnected;
		this.hidden = hidden;
		this.plane = plane;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (player.getMainHandStack().getItem() instanceof ShifterItem) {
			((ShifterItem)player.getMainHandStack().getItem()).changeMode(player, disconnected, hidden, plane);
		}
	}

}
