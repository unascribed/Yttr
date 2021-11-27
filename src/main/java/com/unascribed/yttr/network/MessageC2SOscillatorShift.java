package com.unascribed.yttr.network;

import com.unascribed.yttr.content.block.inred.InRedOscillatorBlockEntity;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.C2SMessage;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MessageC2SOscillatorShift extends C2SMessage {
	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int y;
	@MarshalledAs("varint")
	public int z;
	@MarshalledAs("varint")
	public int shiftValue;

	public MessageC2SOscillatorShift(NetworkContext ctx) {
		super(ctx);
	}

	public MessageC2SOscillatorShift(InRedOscillatorBlockEntity be, int shiftValue) {
		super(YNetwork.CONTEXT);
		this.x = be.getPos().getX();
		this.y = be.getPos().getY();
		this.z = be.getPos().getZ();
		this.shiftValue = shiftValue;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		BlockEntity be = player.getServerWorld().getBlockEntity(new BlockPos(x, y, z));
		if (be instanceof InRedOscillatorBlockEntity) {
			InRedOscillatorBlockEntity oscillator = ((InRedOscillatorBlockEntity) be);
			oscillator.maxRefreshTicks += shiftValue;
			oscillator.setDelay();
			oscillator.sync();
		}
	}
}
