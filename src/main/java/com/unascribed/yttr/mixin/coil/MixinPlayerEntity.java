package com.unascribed.yttr.mixin.coil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YEnchantments;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;

// ensure our RETURN inject comes first so we don't wind up undoing someone else's break speed change
@Mixin(value=PlayerEntity.class, priority=1500)
public class MixinPlayerEntity {

	private float yttr$storedBreakSpeed;
	
	@Inject(at=@At(value="FIELD", target="net/minecraft/entity/player/PlayerEntity.onGround:Z"),
			method="getBlockBreakingSpeed", locals=LocalCapture.CAPTURE_FAILHARD)
	public void storeBreakSpeedBeforeOnGroundCheck(BlockState bs, CallbackInfoReturnable<Float> ci, float breakSpeed) {
		yttr$storedBreakSpeed = breakSpeed;
	}
	
	@Inject(at=@At("RETURN"), method="getBlockBreakingSpeed", locals=LocalCapture.CAPTURE_FAILHARD,
			cancellable=true)
	public void restoreBreakSpeed(BlockState bs, CallbackInfoReturnable<Float> ci) {
		float breakSpeed = ci.getReturnValueF();
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (YEnchantments.STABILIZATION.isPresent() && breakSpeed == yttr$storedBreakSpeed/5
				&& EnchantmentHelper.getLevel(YEnchantments.STABILIZATION.get(), Yttr.trinketsAccess.getSoleTrinket(self)) > 0) {
			ci.setReturnValue(yttr$storedBreakSpeed);
		}
	}

}
