package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.TickTimeTracker;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public interface AccessorMinecraftClient {

	@Accessor("tickProfilerResult")
	ProfileResult yttr$getTickProfilerResult();
	@Accessor("trackingTick")
	void yttr$setTrackingTick(int tick);
	@Accessor("tickTimeTracker")
	TickTimeTracker yttr$getTickTimeTracker();
	
}
