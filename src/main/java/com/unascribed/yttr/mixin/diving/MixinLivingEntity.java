package com.unascribed.yttr.mixin.diving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.mixinsupport.SuitPiecesForJump;
import com.unascribed.yttr.util.EquipmentSlots;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class MixinLivingEntity implements SuitPiecesForJump {

	private int yttr$suitPiecesForJump = 0;
	
	@Inject(at=@At("HEAD"), method="jump")
	public void jumpHead(CallbackInfo ci) {
		int i = 0;
		LivingEntity self = (LivingEntity)(Object)this;
		for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
			ItemStack is = self.getEquippedStack(slot);
			if (is.getItem() instanceof SuitArmorItem) {
				i++;
			}
		}
		yttr$suitPiecesForJump = i;
	}
	
	@Inject(at=@At("TAIL"), method="jump")
	public void jumpTail(CallbackInfo ci) {
		yttr$suitPiecesForJump = 0;
	}
	
	@Inject(at=@At("RETURN"), method="getJumpVelocity", cancellable=true)
	protected void getJumpVelocity(CallbackInfoReturnable<Float> ci) {
		if (yttr$suitPiecesForJump > 0) {
			float m = 1-(0.2f*yttr$suitPiecesForJump);
			ci.setReturnValue(ci.getReturnValueF()*m);
		}
	}
	
	@Override
	public int yttr$getSuitPiecesForJump() {
		return yttr$suitPiecesForJump;
	}
	
}
