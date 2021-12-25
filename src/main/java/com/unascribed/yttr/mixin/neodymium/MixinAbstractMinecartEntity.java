package com.unascribed.yttr.mixin.neodymium;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.Magnetized;

@Mixin(AbstractMinecartEntity.class)
public class MixinAbstractMinecartEntity {
	
	@Inject(at=@At("TAIL"), method="tick")
	protected void tick(CallbackInfo ci) {
		if (this instanceof Magnetized) ((Magnetized)this).yttr$magnetTick();
	}

}
