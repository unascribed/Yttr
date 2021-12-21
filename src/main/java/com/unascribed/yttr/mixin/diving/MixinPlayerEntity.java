package com.unascribed.yttr.mixin.diving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

	@Inject(at=@At("HEAD"), method="canConsume", cancellable=true)
	public void canConsume(boolean alwaysEdible, CallbackInfoReturnable<Boolean> ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (self.getEquippedStack(EquipmentSlot.HEAD).getItem() == YItems.SUIT_HELMET) {
			ci.setReturnValue(false);
		}
	}
	
}
