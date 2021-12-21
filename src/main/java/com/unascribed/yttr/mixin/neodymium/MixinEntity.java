package com.unascribed.yttr.mixin.neodymium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		Entity self = (Entity)(Object)this;
		if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)self;
			if (le.getEquippedStack(EquipmentSlot.FEET).getItem().isIn(YTags.Item.MAGNETIC_BOOTS)) {
				if (self.world.getBlockState(self.getBlockPos().down()).isIn(YTags.Block.MAGNETIC) ||
						self.world.getBlockState(self.getBlockPos()).isIn(YTags.Block.MAGNETIC)) {
					ci.setReturnValue(ci.getReturnValueF()*0.1f);
				}
			}
		}
	}

	@Inject(at=@At("RETURN"), method="getVelocityMultiplier", cancellable=true)
	protected void getVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		Entity self = (Entity)(Object)this;
		if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)self;
			if (le.getEquippedStack(EquipmentSlot.FEET).getItem().isIn(YTags.Item.MAGNETIC_BOOTS)) {
				if (self.world.getBlockState(self.getBlockPos().down()).isIn(YTags.Block.MAGNETIC) ||
						self.world.getBlockState(self.getBlockPos()).isIn(YTags.Block.MAGNETIC)) {
					ci.setReturnValue(ci.getReturnValueF()*0.2f);
				}
			}
		}
	}
	
}
