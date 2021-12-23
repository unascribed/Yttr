package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public interface AccessorClientPlayerInteractionManager {

	@Accessor("blockBreakingCooldown")
	void yttr$setBlockBreakingCooldown(int i);
	
}
