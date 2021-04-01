package com.unascribed.yttr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(HungerManager.class)
public class MixinHungerManager {

	@Unique
	private PlayerEntity yttr$storedPlayer = null;
	
	@Unique
	private static float yttr$calculateModifier(PlayerEntity player) {
		if (player != null) {
			StatusEffectInstance d = player.getStatusEffect(Yttr.DELICACENESS);
			if (d != null) {
				return 1+(d.getAmplifier()+1)*0.05f;
			}
		}
		return 1;
	}
	
	@ModifyVariable(at=@At("HEAD"), method="add", ordinal=0, argsOnly=true)
	public float modifySaturation(float in) {
		return in*yttr$calculateModifier(yttr$storedPlayer);
	}
	
	@ModifyVariable(at=@At("HEAD"), method="add", ordinal=0, argsOnly=true)
	public int modifyFood(int in) {
		return MathHelper.ceil(in*yttr$calculateModifier(yttr$storedPlayer));
	}
	
	@Inject(at=@At("HEAD"), method="update")
	public void update(PlayerEntity player, CallbackInfo ci) {
		yttr$storedPlayer = player;
	}
	
}
