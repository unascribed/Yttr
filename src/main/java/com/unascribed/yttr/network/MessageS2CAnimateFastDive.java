package com.unascribed.yttr.network;

import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import com.google.common.collect.Multiset;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CAnimateFastDive extends S2CMessage {
	
	@MarshalledAs("com.unascribed.yttr.network.marshaller.SuitResourceMultisetMarshaller")
	public Multiset<SuitResource> costs;
	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int z;
	@MarshalledAs("varint")
	public int time;
	
	public MessageS2CAnimateFastDive(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CAnimateFastDive(Multiset<SuitResource> costs, int x, int z, int time) {
		super(YNetwork.CONTEXT);
		this.costs = costs;
		this.x = x;
		this.z = z;
		this.time = time;
	}



	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).startFastDive(costs, x, z, time);
		}
	}

}
