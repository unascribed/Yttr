package com.unascribed.yttr.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;

/**
 * A channel in-bound handler that only forwards received messages to the next
 * channel in-bound handler in the channel pipeline after a random delay between
 * {@link #baseDelay} and {@code baseDelay + }{@link #extraDelay} milliseconds.
 * 
 * @apiNote This may be used to simulate a laggy network enviroment.
 * 
 * Lifted from 1.17.
 */
public class DelayingChannelInboundHandler extends ChannelInboundHandlerAdapter {
	private static final Timer TIMER = new HashedWheelTimer();
	private final int baseDelay;
	private final int extraDelay;
	private final List<Packet> packets = Lists.newArrayList();

	public DelayingChannelInboundHandler(int baseDelay, int extraDelay) {
		this.baseDelay = baseDelay;
		this.extraDelay = extraDelay;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		int delay = baseDelay + ThreadLocalRandom.current().nextInt(extraDelay);
		packets.add(new Packet(ctx, msg));
		TIMER.newTimeout(this::forward, delay, TimeUnit.MILLISECONDS);
	}

	private void forward(Timeout timeout) {
		Packet packet = packets.remove(0);
		packet.context.fireChannelRead(packet.message);
	}

	private static final class Packet {
		public final ChannelHandlerContext context;
		public final Object message;

		public Packet(ChannelHandlerContext context, Object message) {
			this.context = context;
			this.message = message;
		}
	}
}