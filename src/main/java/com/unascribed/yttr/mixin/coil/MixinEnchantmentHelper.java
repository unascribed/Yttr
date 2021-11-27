package com.unascribed.yttr.mixin.coil;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.enchant.CoilEnchantment;
import com.unascribed.yttr.init.YItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

	@Inject(at=@At("RETURN"), method="getPossibleEntries")
	private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> ci) {
		if (!YItems.CUPROSTEEL_COIL.is(stack.getItem())) {
			ci.getReturnValue().removeIf(ele -> ele != null && ele.enchantment instanceof CoilEnchantment);
		}
	}
	
}
