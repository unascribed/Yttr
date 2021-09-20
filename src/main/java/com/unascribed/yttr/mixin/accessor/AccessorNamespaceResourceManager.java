package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.NamespaceResourceManager;

@Mixin(NamespaceResourceManager.class)
public interface AccessorNamespaceResourceManager {

	@Accessor("namespace")
	String yttr$getNamespace();
	
}
