package com.unascribed.yttr.network;

import java.util.List;

import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import com.unascribed.yttr.world.Geyser;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDive extends S2CMessage {
	
	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int z;
	public List<Geyser> geysers;
	
	public MessageS2CDive(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDive(int x, int z, List<Geyser> geysers) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.z = z;
		this.geysers = geysers;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		mc.openScreen(new SuitScreen(x, z, geysers));
	}

}
