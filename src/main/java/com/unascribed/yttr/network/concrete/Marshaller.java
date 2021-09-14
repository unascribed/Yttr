package com.unascribed.yttr.network.concrete;

import net.minecraft.network.PacketByteBuf;

/**
 * Handles the serializing and deserializing of a type. Should define a static
 * field named "INSTANCE" containing a singleton.
 */
public interface Marshaller<T> {
	T unmarshal(PacketByteBuf in);
	void marshal(PacketByteBuf out, T t);
}
