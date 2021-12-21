package com.unascribed.yttr.mixin.diving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.SuitPiecesForJump;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(at=@At("HEAD"), method="isSprinting", cancellable=true)
	public void isSprinting(CallbackInfoReturnable<Boolean> ci) {
		if (this instanceof SuitPiecesForJump && ((SuitPiecesForJump)this).yttr$getSuitPiecesForJump() > 1) {
			ci.setReturnValue(false);
		}
	}
	
	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		if (this instanceof SuitPiecesForJump && ((SuitPiecesForJump)this).yttr$getSuitPiecesForJump() > 1) {
			float m = 1-(0.2f*((SuitPiecesForJump)this).yttr$getSuitPiecesForJump());
			ci.setReturnValue(ci.getReturnValueF()*m);
		}
	}
	
	
}
