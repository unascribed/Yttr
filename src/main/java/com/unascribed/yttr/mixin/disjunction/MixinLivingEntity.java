package com.unascribed.yttr.mixin.disjunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.enchant.DisjunctionEnchantment;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Inject(at=@At("RETURN"), method="getGroup", cancellable=true)
	public void getGroup(CallbackInfoReturnable<EntityGroup> ci) {
		if (ci.getReturnValue() == EntityGroup.DEFAULT) {
			Object self = this;
			if (self instanceof EndermanEntity || self instanceof EndermiteEntity) {
				ci.setReturnValue(DisjunctionEnchantment.ENDER);
			}
		}
	}
	
}
