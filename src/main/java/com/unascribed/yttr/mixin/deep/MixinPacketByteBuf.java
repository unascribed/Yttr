package com.unascribed.yttr.mixin.deep;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {

	@Inject(at=@At("HEAD"), method="writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;", cancellable=true)
	public void writeItemStack(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> ci) {
		if (stack.getCount() > 127) {
			PacketByteBuf buf = (PacketByteBuf)(Object)this;
			// 145 chosen randomly in the hopes to not conflict if someone else is doing a similar thing
			buf.writeByte(145);
			Item item = stack.getItem();
			buf.writeVarInt(Item.getRawId(item));
			buf.writeVarInt(stack.getCount()); // here's the change: count is a varint, not a byte
			NbtCompound compoundTag = null;
			if (item.isDamageable() || item.shouldSyncTagToClient()) {
				compoundTag = stack.getTag();
			}
			buf.writeNbt(compoundTag);
			ci.setReturnValue(buf);
		}
	}
	
	@Inject(at=@At("HEAD"), method="readItemStack()Lnet/minecraft/item/ItemStack;", cancellable=true)
	public void readItemStack(CallbackInfoReturnable<ItemStack> ci) {
		PacketByteBuf buf = (PacketByteBuf)(Object)this;
		// read the byte without advancing
		if (buf.getUnsignedByte(buf.readerIndex()) == 145) {
			// advance the one byte now that we know it's us
			buf.skipBytes(1);
			int item = buf.readVarInt();
			int count = buf.readVarInt();
			ItemStack stack = new ItemStack(Item.byRawId(item), count);
			stack.setTag(buf.readNbt());
			ci.setReturnValue(stack);
		}
	}
	
}
