package com.unascribed.yttr.mixin.potion.mercurial;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YStatusEffects;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;

@Mixin(PotionUtil.class)
public class MixinPotionUtil {

	@Inject(at=@At("RETURN"), method="getPotionEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;")
	private static void getPotionEffects(ItemStack stack, CallbackInfoReturnable<List<StatusEffectInstance>> ci) {
		if (stack.getItem() == YItems.MERCURIAL_POTION || stack.getItem() == YItems.MERCURIAL_SPLASH_POTION) {
			List<StatusEffectInstance> li = ci.getReturnValue();
			for (int i = 0; i < li.size(); i++) {
				StatusEffectInstance orig = li.get(i);
				li.set(i, new StatusEffectInstance(orig.getEffectType(), orig.getDuration(), ((orig.getAmplifier()+1)*2)-1));
			}
			li.add(new StatusEffectInstance(YStatusEffects.POTION_SICKNESS, 20*120, 0));
		}
	}
	
}
