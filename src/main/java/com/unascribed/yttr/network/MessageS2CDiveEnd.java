package com.unascribed.yttr.network;

import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.client.suit.SuitSound;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDiveEnd extends S2CMessage {
	
	public MessageS2CDiveEnd(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDiveEnd() {
		super(YNetwork.CONTEXT);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			mc.getSoundManager().play(new SuitSound(YSounds.DIVE_END));
			mc.openScreen(null);
		}
	}

}
