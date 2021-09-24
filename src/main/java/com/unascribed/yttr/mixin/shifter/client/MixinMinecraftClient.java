package com.unascribed.yttr.mixin.shifter.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.content.item.ShifterItem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	
	private int yttr$storedPickSlot = -1;
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/entity/player/PlayerInventory.getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"),
			method="doItemPick", ordinal=0)
	public int modifyPickFromSlotBefore(int orig) {
		if (orig == -1) return orig;
		if (player != null && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ShifterItem) {
			yttr$storedPickSlot = orig;
			// ensure this is not a valid hotbar slot index so the client sends a packet for it
			return 9000;
		} else {
			yttr$storedPickSlot = -1;
		}
		return orig;
	}
	
	@ModifyVariable(at=@At(value="FIELD", target="net/minecraft/client/MinecraftClient.interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;"),
			method="doItemPick", ordinal=0)
	public int modifyPickFromSlotAfter(int orig) {
		if (yttr$storedPickSlot != -1) {
			// change it back so the packet is valid
			return yttr$storedPickSlot;
		}
		return orig;
	}
	
}
