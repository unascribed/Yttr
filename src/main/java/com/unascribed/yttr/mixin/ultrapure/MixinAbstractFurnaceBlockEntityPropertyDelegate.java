package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="net/minecraft/block/entity/AbstractFurnaceBlockEntity$1")
public abstract class MixinAbstractFurnaceBlockEntityPropertyDelegate {

	private boolean yttr$reentering = false;
	
	@Shadow
    public abstract int get(int index);
	
	@Inject(at=@At("HEAD"), method="get", cancellable=true)
    public void get(int index, CallbackInfoReturnable<Integer> ci) {
		if (yttr$reentering) return;
		try {
			yttr$reentering = true;
			if (index == 0) {
				int m = get(1);
				if (m > 32767) {
					int v = get(0);
					int newV = (int)((v/(float)m)*32767);
					ci.setReturnValue(newV);
				}
			}
			if (index == 1) {
				int m = get(1);
				if (m > 32767) {
					ci.setReturnValue(32767);
				}
			}
		} finally {
			yttr$reentering = false;
		}
     }
	
}
