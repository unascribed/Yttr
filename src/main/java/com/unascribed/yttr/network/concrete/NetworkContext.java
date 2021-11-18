package com.unascribed.yttr.network.concrete;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.mixin.accessor.AccessorCustomPayload;
import com.unascribed.yttr.network.concrete.annotation.field.Optional;
import com.unascribed.yttr.network.concrete.exception.BadMessageException;
import com.unascribed.yttr.network.concrete.exception.WrongSideException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public final class NetworkContext {
	public static final List<NetworkContext> contexts = new ArrayList<>();
	
	protected static final Map<Class<? extends Message>, MethodHandle> instanciators = Maps.newHashMap();
	
	protected final BiMap<Class<? extends Message>, Integer> packetIds = HashBiMap.create();
	protected final Map<Class<? extends Message>, List<WireField<?>>> marshallers = Maps.newHashMap();
	protected final Multiset<Class<? extends Message>> booleanCount = HashMultiset.create();
	protected final Multiset<Class<? extends Message>> optionalCount = HashMultiset.create();
	
	protected final Identifier channel;
	
	private int nextPacketId = 0;
	
	private NetworkContext(Identifier channel) {
		this.channel = channel;
		contexts.add(this);
	}
	
	public NetworkContext register(Class<? extends Message> clazz) {
		if (packetIds.containsKey(clazz)) {
			return this;
		}
		packetIds.put(clazz, nextPacketId++);
		List<WireField<?>> fields = Lists.newArrayList();
		Class<?> cursor = clazz;
		while (cursor != null && cursor != Object.class) {
			for (Field f : cursor.getDeclaredFields()) {
				if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
					if (f.getType() == Boolean.TYPE) {
						booleanCount.add(clazz);
					}
					if (f.getAnnotation(Optional.class) != null) {
						optionalCount.add(clazz);
					}
					WireField<?> wf = new WireField<>(f);
					fields.add(wf);
				}
			}
			cursor = cursor.getSuperclass();
		}
		marshallers.put(clazz, fields);
		return this;
	}
	
	
	public Identifier getChannel() {
		return channel;
	}
	
	
	
	protected PacketByteBuf getPayloadFrom(Message m) {
		if (!packetIds.containsKey(m.getClass())) throw new BadMessageException(m.getClass() + " is not registered");
		PacketByteBuf payload = new PacketByteBuf(Unpooled.buffer());
		payload.writeByte(packetIds.get(m.getClass()));
		int bools = booleanCount.count(m.getClass()) + optionalCount.count(m.getClass());
		if (bools > 0) {
			List<Boolean> li = Lists.newArrayListWithCapacity(bools);
			for (WireField<?> wf : marshallers.get(m.getClass())) {
				if (wf.getType() == Boolean.TYPE) {
					li.add((Boolean) wf.get(m));
				} else if (wf.isOptional()) {
					li.add(wf.get(m) != null);
				}
			}
			for (int i = 0; i < (bools + 7) / 8; i++) {
				int by = 0;
				for (int j = i * 8; j < Math.min(li.size(), i + 8); j++) {
					if (li.get(j)) {
						by |= (1 << j);
					}
				}
				payload.writeByte(by);
			}
		}
		marshallers.get(m.getClass()).stream()
		                             .filter((it) -> it.getType() != Boolean.TYPE)
		                             .forEach((it) -> it.marshal(m, payload));
		return payload;
	}


	public boolean handleCustomPacket(ServerPlayNetworkHandler handler, CustomPayloadC2SPacket pkt) {
		if (((AccessorCustomPayload)pkt).yttr$getChannel().equals(channel)) {
			try {
				PacketByteBuf payload = ((AccessorCustomPayload)pkt).yttr$getData();
				Message m = readPacket(EnvType.SERVER, payload);
				m.doHandleServer(handler.player);
			} catch (Throwable t) {
				YLog.warn("Exception thrown during packet handling, kicking player", t);
				handler.disconnect(new LiteralText("Internal server error"));
			}
			return true;
		}
		return false;
	}
	
	@Environment(EnvType.CLIENT)
	public boolean handleCustomPacket(ClientPlayNetworkHandler handler, CustomPayloadS2CPacket pkt) {
		if (pkt.getChannel().equals(channel)) {
			PacketByteBuf payload = pkt.getData();
			Message m = readPacket(EnvType.CLIENT, payload);
			m.doHandleClient();
			return true;
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private Message readPacket(EnvType env, PacketByteBuf payload) {
		int id = payload.readUnsignedByte();
		if (!packetIds.containsValue(id)) {
			throw new IllegalArgumentException("Unknown packet id " + id);
		}
		Class<? extends Message> clazz = packetIds.inverse().get(id);
		Message m;
		try {
			m = instantiateMessage(clazz);
		} catch (Throwable t) {
			throw new BadMessageException("Cannot instanciate message class " + clazz, t);
		}
		if (m.getEnv() != env) {
			throw new WrongSideException("Cannot receive packet of type " + clazz + " in environment " + env);
		}
		Set<WireField<?>> present = Sets.newHashSetWithExpectedSize(marshallers.get(m.getClass()).size());
		int bools = booleanCount.count(m.getClass()) + optionalCount.count(m.getClass());
		if (bools > 0) {
			List<Consumer<Boolean>> li = Lists.newArrayListWithCapacity(bools);
			for (WireField<?> wf : marshallers.get(m.getClass())) {
				if (wf.getType() == Boolean.TYPE) {
					li.add((b) -> ((WireField<Boolean>) wf).set(m, b));
					present.add(wf);
				} else if (wf.isOptional()) {
					li.add((b) -> { if (b) { present.add(wf); } });
				} else {
					present.add(wf);
				}
			}
			for (int i = 0; i < (bools + 7) / 8; i++) {
				int by = payload.readUnsignedByte();
				for (int j = i * 8; j < Math.min(li.size(), i + 8); j++) {
					boolean val = (by & (1 << (j - i))) != 0;
					li.get(j).accept(val);
				}
			}
		} else {
			present.addAll(marshallers.get(m.getClass()));
		}
		marshallers.get(m.getClass()).stream()
		                             .filter((it) -> it.getType() != Boolean.TYPE && present.contains(it))
		                             .forEach((it) -> it.unmarshal(m, payload));
		return m;
	}
	
	private Message instantiateMessage(Class<? extends Message> clazz) throws Throwable {
		MethodHandle instanciator = instanciators.get(clazz);
		if (instanciator == null) {
			Constructor<? extends Message> cons;
			try {
				cons = clazz.getDeclaredConstructor(NetworkContext.class);
			} catch (Throwable t) {
				cons = clazz.getDeclaredConstructor();
			}
			instanciator = MethodHandles.lookup().unreflectConstructor(cons);
			instanciators.put(clazz, instanciator);
		}
		try {
			return (Message)instanciator.invoke(this);
		} catch (Throwable t) {
			return (Message)instanciator.invoke();
		}
	}
	
	
	public static NetworkContext forChannel(String channel) {
		return new NetworkContext(new Identifier(channel));
	}


}
