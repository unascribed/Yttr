package com.unascribed.yttr.mixin.mender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mechanics.BedrockMender;

import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractBlockState.class)
public class MixinAbstractBlockState {

	@Inject(at=@At("HEAD"), method="onStateReplaced")
	public void onStateReplaced(World world, BlockPos pos, BlockState state, boolean moved, CallbackInfo ci) {
		BedrockMender.onStateReplaced((BlockState)(Object)this, world, pos, state, moved);
	}
	
}
