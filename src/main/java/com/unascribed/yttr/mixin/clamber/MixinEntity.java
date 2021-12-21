package com.unascribed.yttr.mixin.clamber;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		Entity self = (Entity)(Object)this;
		if (self.isSneaking() && self.world.getBlockState(self.getBlockPos().down()).isIn(YTags.Block.CLAMBER_BLOCKS)) {
			ci.setReturnValue(0.05f);
		}
	}
	
}
