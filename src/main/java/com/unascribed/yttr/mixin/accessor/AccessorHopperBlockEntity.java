package com.unascribed.yttr.mixin.accessor;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.entity.HopperBlockEntity;

@Mixin(HopperBlockEntity.class)
public interface AccessorHopperBlockEntity {

	@Accessor("lastTickTime")
	long yttr$getLastTickTime();
	@Accessor("lastTickTime")
	void yttr$setLastTickTime(long v);
	
	@Accessor("transferCooldown")
	int yttr$getTransferCooldown();
	@Accessor("transferCooldown")
	void yttr$setTransferCooldown(int v);
	
	@Invoker("needsCooldown")
	boolean yttr$needsCooldown();
	
	@Invoker("setCooldown")
	void yttr$setCooldown(int v);
	
	@Invoker("insertAndExtract")
	boolean yttr$insertAndExtract(Supplier<Boolean> extractMethod);
	
}
