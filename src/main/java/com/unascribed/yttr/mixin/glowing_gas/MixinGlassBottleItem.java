package com.unascribed.yttr.mixin.glowing_gas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.mechanics.GlowingGasLogic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(GlassBottleItem.class)
public abstract class MixinGlassBottleItem {

	@Inject(at=@At("HEAD"), method="use", cancellable=true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		ItemStack stack = user.getStackInHand(hand);
		if (GlowingGasLogic.consumeGasCloud(world, user.getBoundingBox().expand(2))) {
			YStats.add(user, YStats.GLOWDAMP_COLLECTED, 1);
			ci.setReturnValue(TypedActionResult.success(fill(stack, user, new ItemStack(YItems.GLOWING_GAS)), world.isClient()));
		}
	}

	@Shadow
	protected abstract ItemStack fill(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2);

}
