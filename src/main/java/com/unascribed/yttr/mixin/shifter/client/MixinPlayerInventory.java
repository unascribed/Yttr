package com.unascribed.yttr.mixin.shifter.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.item.ShifterItem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {

	@Shadow @Final
	public PlayerEntity player;
	
	@Shadow
	public abstract int getSlotWithStack(ItemStack stack);
	
	@Inject(at=@At("HEAD"), method="addPickBlock", cancellable=true)
	public void addPickBlock(ItemStack stack, CallbackInfo ci) {
		if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ShifterItem) {
			int slot = getSlotWithStack(stack);
			if (slot != -1) {
				stack = player.inventory.getStack(slot);
				player.inventory.setStack(slot, player.getStackInHand(Hand.OFF_HAND));
			}
			player.setStackInHand(Hand.OFF_HAND, stack);
			MinecraftClient.getInstance().interactionManager.clickCreativeStack(player.getStackInHand(Hand.OFF_HAND), 36+9);
			ci.cancel();
		}
	}
	
}
