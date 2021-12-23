package com.unascribed.yttr.mixin.accessor.client;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;

@Environment(EnvType.CLIENT)
@Mixin(ResourcePackManager.class)
public interface AccessorResourcePackManager {

	@Accessor("providers")
	Set<ResourcePackProvider> yttr$getProviders();
	@Accessor("providers")
	void yttr$setProviders(Set<ResourcePackProvider> v);
	
}
