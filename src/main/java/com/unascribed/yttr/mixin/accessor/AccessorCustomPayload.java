package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

@Mixin(CustomPayloadC2SPacket.class)
public interface AccessorCustomPayload {

	@Accessor("channel")
	Identifier yttr$getChannel();
	@Accessor("data")
	PacketByteBuf yttr$getData();
	
}
