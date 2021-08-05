package com.unascribed.yttr.mixin.potion.mercurial;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YStatusEffects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(PotionItem.class)
public class MixinPotionItem {

	@Inject(at=@At("HEAD"), method="use", cancellable=true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		if (user.hasStatusEffect(YStatusEffects.POTION_SICKNESS)) {
			ci.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
		}
	}
	
}
