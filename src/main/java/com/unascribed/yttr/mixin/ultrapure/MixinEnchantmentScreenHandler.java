package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
			} else if (inSlot.getItem() == YItems.ULTRAPURE_LAZURITE) {
				if (!insertItem(inSlot, 1, 2, true)) {
					ci.setReturnValue(ItemStack.EMPTY);
					return;
				}
			}
		}
	}
}
