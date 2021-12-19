package com.unascribed.yttr.mixin.effector.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(ClientChunkManager.class)
public class MixinClientChunkManager {
	
	@Inject(at=@At("HEAD"), method="shouldTickBlock", cancellable=true)
	public void shouldTickBlock(BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
		Object self = this;
		YttrWorld yw;
		BlockView bv = ((ClientChunkManager)self).getWorld();
		if (bv instanceof YttrWorld) {
			yw = (YttrWorld)bv;
		} else {
			return;
		}
		if (yw.yttr$isPhased(pos)) {
			ci.setReturnValue(false);
		}
	}
	
	
}
