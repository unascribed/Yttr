package com.unascribed.yttr.mixin.cleaver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/entity/effect/StatusEffectUtil.hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"),
			method="getBlockBreakingSpeed")
	public float addCleaverEnchantmentPenalty(float breakSpeed) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (breakSpeed > 1) {
			int lvl = EnchantmentHelper.getEfficiency(self);
			ItemStack main = self.getMainHandStack();
			if (lvl > 0 && main.getItem() == YItems.REINFORCED_CLEAVER) {
				breakSpeed -= ((lvl * lvl) + 1)*0.5f;
			}
		}
		return breakSpeed;
	}

}
