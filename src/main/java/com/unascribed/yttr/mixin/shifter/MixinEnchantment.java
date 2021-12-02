
package com.unascribed.yttr.mixin.shifter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

@Mixin(Enchantment.class)
public class MixinEnchantment {

	@Inject(at=@At("HEAD"), method="isAcceptableItem", cancellable=true)
	public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if ((Object)this == Enchantments.SILK_TOUCH && stack.getItem() == YItems.SHIFTER) {
			ci.setReturnValue(true);
		}
	}

}
