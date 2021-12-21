package com.unascribed.yttr.mixin.coil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Entity.class)
public class MixinEntity {
	
	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		Object self = this;
		if (self instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)self;
			if (p.isSneaking()) return;
			int level = Yttr.getSpringingLevel(p);
			if (level > 0) {
				ci.setReturnValue(ci.getReturnValueF()+(level/4f));
			}
		}
	}
	
}
