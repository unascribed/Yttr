package com.unascribed.yttr.mixin.effector.client;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World implements YttrWorld {

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
		super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
	}

	@Shadow
	private WorldRenderer worldRenderer;
	
	@Override
	public void yttr$scheduleRenderUpdate(BlockPos pos) {
		// state arguments aren't used, so don't waste time retrieving information
		worldRenderer.updateBlock(this, pos, null, null, 8);
	}
	
}
