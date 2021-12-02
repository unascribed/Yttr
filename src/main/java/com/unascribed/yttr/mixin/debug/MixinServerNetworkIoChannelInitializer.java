package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.util.DelayingChannelInboundHandler;
import com.unascribed.yttr.util.YLog;

import io.netty.channel.Channel;

@Mixin(targets="net/minecraft/server/ServerNetworkIo$2")
public class MixinServerNetworkIoChannelInitializer {

	@Inject(at=@At(value="CONSTANT", args="stringValue=packet_handler"), method="initChannel")
	protected void initChannel(Channel channel, CallbackInfo ci) throws Exception {
		if (YConfig.Debug.simulateLatency > 0) {
			YLog.warn("Simulating a network latency of {}ms with a jitter of {}ms, as requested in the config!", YConfig.Debug.simulateLatency, YConfig.Debug.simulateLatencyJitter);
			channel.pipeline().addLast(new DelayingChannelInboundHandler(YConfig.Debug.simulateLatency, YConfig.Debug.simulateLatencyJitter));
		}
	}
	
}
