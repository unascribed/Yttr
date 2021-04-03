package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.minecraft.item.Item;

// WEAPON and DIGGER
@Mixin(targets={"net/minecraft/enchantment/EnchantmentTarget$11", "net/minecraft/enchantment/EnchantmentTarget$12"})
public class MixinEnchantmentTargets {

	@Inject(at=@At("HEAD"), method="isAcceptableItem", cancellable=true)
	public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> ci) {
		if (item == Yttr.REINFORCED_CLEAVER) {
			ci.setReturnValue(true);
		}
	}
	
}
