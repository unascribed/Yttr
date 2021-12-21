package com.unascribed.yttr.mixin.clamber;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Inject(at=@At("HEAD"), method="isHoldingOntoLadder", cancellable=true)
	public void isHoldingOntoLadder(CallbackInfoReturnable<Boolean> ci) {
		LivingEntity self = (LivingEntity)(Object)this;
		if (self.getClimbingPos().isPresent()) {
			BlockPos pos = self.getClimbingPos().get();
			if (self.world.getBlockState(pos).isIn(YTags.Block.CLAMBER_BLOCKS)) {
				ci.setReturnValue(false);
			}
		}
	}
	
}
