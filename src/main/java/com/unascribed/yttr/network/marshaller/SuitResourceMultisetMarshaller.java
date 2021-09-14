package com.unascribed.yttr.network.marshaller;

import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.network.concrete.Marshaller;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;

import net.minecraft.network.PacketByteBuf;

public class SuitResourceMultisetMarshaller implements Marshaller<Multiset<SuitResource>> {

	@Override
	public Multiset<SuitResource> unmarshal(PacketByteBuf in) {
		Multiset<SuitResource> ms = EnumMultiset.create(SuitResource.class);
		for (SuitResource sr : SuitResource.VALUES) {
			ms.add(sr, in.readVarInt());
		}
		return ms;
	}

	@Override
	public void marshal(PacketByteBuf out, Multiset<SuitResource> t) {
		for (SuitResource sr : SuitResource.VALUES) {
			out.writeVarInt(t.count(sr));
		}
	}

}
