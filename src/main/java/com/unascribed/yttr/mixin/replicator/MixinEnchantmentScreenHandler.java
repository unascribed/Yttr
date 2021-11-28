package com.unascribed.yttr.mixin.replicator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

@Mixin(EnchantmentScreenHandler.class)
public abstract class MixinEnchantmentScreenHandler extends ScreenHandler {

	protected MixinEnchantmentScreenHandler(ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(at=@At("HEAD"), method="transferSlot", cancellable=true)
	public void transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> ci) {
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack inSlot = slot.getStack();
			if (index == 0 || index == 1) {
				return;
			} else if (inSlot.getItem() == YItems.REPLICATOR && slots.get(1).canInsert(ReplicatorBlockItem.getHeldItem(inSlot))) {
				if (!insertItem(inSlot, 1, 2, true)) {
					ci.setReturnValue(ItemStack.EMPTY);
					return;
				}
			}
		}
	}
	
	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isEmpty()Z"), method="onButtonClick", ordinal=1)
	public ItemStack modifyLapisStack(ItemStack lapis) {
		if (lapis.getItem() == YItems.REPLICATOR && slots.get(1).canInsert(ReplicatorBlockItem.getHeldItem(lapis))) {
			ItemStack held = ReplicatorBlockItem.getHeldItem(lapis).copy();
			held.setCount(64);
			return held;
		}
		return lapis;
	}
	
	@Inject(at=@At("HEAD"), method="getLapisCount", cancellable=true)
	public void getLapisCount(CallbackInfoReturnable<Integer> ci) {
		ItemStack lapis = slots.get(1).getStack();
		if (lapis.getItem() == YItems.REPLICATOR && slots.get(1).canInsert(ReplicatorBlockItem.getHeldItem(lapis))) {
			ci.setReturnValue(64);
		}
	}
	
}
