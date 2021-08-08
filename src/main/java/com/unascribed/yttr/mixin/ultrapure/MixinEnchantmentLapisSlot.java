package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

@Mixin(targets="net/minecraft/screen/EnchantmentScreenHandler$3")
public abstract class MixinEnchantmentLapisSlot extends Slot {

	public MixinEnchantmentLapisSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Inject(at=@At("HEAD"), method="canInsert(Lnet/minecraft/item/ItemStack;)Z", cancellable=true)
	public void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (stack.getItem() == YItems.ULTRAPURE_LAZURITE) {
			ci.setReturnValue(true);
		}
	}
	
}
