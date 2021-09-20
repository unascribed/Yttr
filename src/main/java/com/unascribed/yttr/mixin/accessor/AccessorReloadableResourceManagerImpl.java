package com.unascribed.yttr.mixin.accessor;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.ReloadableResourceManagerImpl;

@Mixin(ReloadableResourceManagerImpl.class)
public interface AccessorReloadableResourceManagerImpl {

	@Accessor("namespaces")
	Set<String> yttr$getAllNamespaces();
	
}
