package com.unascribed.yttr.mixin.coil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	
	@Inject(at=@At("RETURN"), method="getJumpVelocity", cancellable=true)
	protected void getJumpVelocity(CallbackInfoReturnable<Float> ci) {
		Object self = this;
		if (self instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)self;
			if (p.isSneaking()) return;
			int level = Yttr.getSpringingLevel(p);
			if (level > 0) {
				ci.setReturnValue(ci.getReturnValueF()*(1+(level/4f)));
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="jump")
	public void jump(CallbackInfo ci) {
		Object self = this;
		if (self instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)self;
			if (p.isSneaking()) return;
			int level = Yttr.getSpringingLevel(p);
			if (level > 0 && Yttr.isWearingCoil(p)) {
				Yttr.getSoleTrinket.apply(p).damage(level/2, p, (e) -> {
					e.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1, 1);
				});
			}
		}
	}
	
}
