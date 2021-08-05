package com.unascribed.yttr.mixin.potion.mercurial;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.item.ItemStack;

@Mixin(targets="net/minecraft/screen/BrewingStandScreenHandler$PotionSlot")
public class MixinPotionSlot {

	@Inject(at=@At("HEAD"), method="matches(Lnet/minecraft/item/ItemStack;)Z", cancellable=true)
	private static void matches(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (stack.getItem() == YItems.MERCURIAL_POTION || stack.getItem() == YItems.MERCURIAL_SPLASH_POTION) {
			ci.setReturnValue(true);
		}
	}
	
}
