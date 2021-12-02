package com.unascribed.yttr.network;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2STrustedRifleFire extends C2SMessage {

	@MarshalledAs("varint")
	public int remainingUseTicks;
	
	public MessageC2STrustedRifleFire(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2STrustedRifleFire(int remainingUseTicks) {
		super(YNetwork.CONTEXT);
		this.remainingUseTicks = remainingUseTicks;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (!YConfig.General.trustPlayers) return;
		ItemStack is = player.getMainHandStack();
		if (is.getItem() instanceof RifleItem) {
			if (remainingUseTicks <= 0) {
				((RifleItem)player.getMainHandStack().getItem()).finishUsing(is, player.world, player);
			} else {
				((RifleItem)player.getMainHandStack().getItem()).doOnStoppedUsing(is, player.world, player, remainingUseTicks);
			}
			player.clearActiveItem();
		}
	}

}
