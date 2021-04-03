package com.unascribed.yttr.mixin.cleaver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;

@Mixin(MobEntity.class)
public class MixinMobEntity {

	@Inject(at=@At("RETURN"), method="tryAttack")
	public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> ci) {
		if (target instanceof LivingEntity && ci.getReturnValueZ()) {
			MobEntity self = (MobEntity)(Object)this;
			ItemStack weapon = self.getEquippedStack(EquipmentSlot.MAINHAND);
			if (weapon.getItem() == YItems.REINFORCED_CLEAVER) {
				YItems.REINFORCED_CLEAVER.postHit(weapon, (LivingEntity)target, self);
			}
		}
	}
	
}
