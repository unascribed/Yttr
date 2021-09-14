package com.unascribed.yttr.network.concrete;

import net.minecraft.network.PacketByteBuf;

public interface Marshallable {
	void writeToNetwork(PacketByteBuf buf);
	void readFromNetwork(PacketByteBuf buf);
}
