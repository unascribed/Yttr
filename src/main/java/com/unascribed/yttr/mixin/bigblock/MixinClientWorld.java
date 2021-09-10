package com.unascribed.yttr.mixin.bigblock;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.BigBlock;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
		super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
	}

	@Inject(at=@At("HEAD"), method="setBlockBreakingInfo(ILnet/minecraft/util/math/BlockPos;I)V", cancellable=true)
	public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo ci) {
		if (BigBlock.handleSetBlockBreakingInfo(this, entityId, pos, progress)) {
			ci.cancel();
		}
	}
	
}
