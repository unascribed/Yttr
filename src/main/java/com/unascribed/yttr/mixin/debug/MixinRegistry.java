package com.unascribed.yttr.mixin.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;
import com.unascribed.yttr.YConfig;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@Mixin(Registry.class)
public class MixinRegistry {
	
	private static PrintStream YTTR$REGISTRY_LOG;
	
	@Inject(at=@At("HEAD"), method="register(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;")
	private static void register(Registry<?> registry, Identifier id, Object entry, CallbackInfoReturnable<Object> ci) {
		if (!YConfig.Debug.registries) return;
		YTTR$REGISTRY_LOG.println("Registering "+registry.getKey().getValue()+" / "+id+" from "+Thread.currentThread().getName());
	}
	
	@Inject(at=@At("HEAD"), method="register(Lnet/minecraft/util/registry/Registry;ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")
	private static void register(Registry<?> registry, int rawId, String id, Object entry, CallbackInfoReturnable<Object> ci) {
		if (!YConfig.Debug.registries) return;
		YTTR$REGISTRY_LOG.println("Replacing "+registry.getKey().getValue()+" / "+id+" from "+Thread.currentThread().getName());
	}

	@Inject(at=@At("HEAD"), method="create(Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/util/registry/MutableRegistry;")
	private static void create(RegistryKey<?> key, MutableRegistry<?> registry, Supplier<?> defaultEntry, Lifecycle lifecycle, CallbackInfoReturnable<MutableRegistry<?>> ci) {
		if (!YConfig.Debug.registries) return;
		if (YTTR$REGISTRY_LOG == null) {
			try {
				YTTR$REGISTRY_LOG = new PrintStream(new FileOutputStream(new File("registry.log")), true);
			} catch (FileNotFoundException e) {
				throw new Error(e);
			}
		}
		YTTR$REGISTRY_LOG.println("Creating "+key.getValue()+" from "+Thread.currentThread().getName());
	}
	
}
