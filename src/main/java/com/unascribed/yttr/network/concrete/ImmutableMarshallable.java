package com.unascribed.yttr.network.concrete;

import net.minecraft.network.PacketByteBuf;

public interface ImmutableMarshallable {
	void writeToNetwork(PacketByteBuf buf);
	/**
	 * @deprecated This method doesn't do anything. It's here as a template for the static method implementers need to define.
	 */
	static void readFromNetwork(PacketByteBuf buf) {}
}
