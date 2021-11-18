package com.unascribed.yttr.network.concrete;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.network.PacketByteBuf;

public class ImmutableMarshallableMarshaller<T extends ImmutableMarshallable> implements Marshaller<T> {
	private final MethodHandle readFromNetwork;
	
	public ImmutableMarshallableMarshaller(Class<T> clazz) {
		try {
			readFromNetwork = MethodHandles.publicLookup().findStatic(clazz, "readFromNetwork", MethodType.methodType(clazz, PacketByteBuf.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public T unmarshal(PacketByteBuf in) {
		try {
			return (T)readFromNetwork.invoke(in);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void marshal(PacketByteBuf out, T t) {
		t.writeToNetwork(out);
	}

}
