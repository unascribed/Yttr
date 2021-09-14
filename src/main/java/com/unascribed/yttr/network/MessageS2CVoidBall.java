package com.unascribed.yttr.network;

import com.unascribed.yttr.client.particle.VoidBallParticle;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CVoidBall extends S2CMessage {

	@MarshalledAs("f32")
	public float x;
	@MarshalledAs("f32")
	public float y;
	@MarshalledAs("f32")
	public float z;
	@MarshalledAs("f32")
	public float r;
	
	public MessageS2CVoidBall(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CVoidBall(float x, float y, float z, float r) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		mc.particleManager.addParticle(new VoidBallParticle(mc.world, x, y, z, r));
	}

}
