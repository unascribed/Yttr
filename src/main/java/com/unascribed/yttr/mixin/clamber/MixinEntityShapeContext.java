package com.unascribed.yttr.mixin.clamber;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.GetEntity;

@Mixin(EntityShapeContext.class)
public class MixinEntityShapeContext implements GetEntity {

	private Entity yttr$entity;
	
	@Inject(at=@At("RETURN"), method="<init>(Lnet/minecraft/entity/Entity;)V")
	public void init(Entity e, CallbackInfo ci) {
		this.yttr$entity = e;
	}

	@Override
	public @Nullable Entity yttr$getEntity() {
		return yttr$entity;
	}
	
}
