package com.unascribed.yttr.network;

import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDivePressure extends S2CMessage {
	
	@MarshalledAs("varint")
	public int pressure;
	
	public MessageS2CDivePressure(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDivePressure(int pressure) {
		super(YNetwork.CONTEXT);
		this.pressure = pressure;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).setPressure(pressure);
		}
	}

}
