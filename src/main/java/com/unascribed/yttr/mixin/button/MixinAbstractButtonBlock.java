package com.unascribed.yttr.mixin.button;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.mechanism.YttriumButtonBlock;

import net.minecraft.block.AbstractButtonBlock;

@Mixin(AbstractButtonBlock.class)
public class MixinAbstractButtonBlock {

	@Inject(at=@At("HEAD"), method="getPressTicks", cancellable=true)
	public void getPressTicks(CallbackInfoReturnable<Integer> ci) {
		Object self = this;
		if (self instanceof YttriumButtonBlock) {
			ci.setReturnValue(2);
		}
	}
	
}
