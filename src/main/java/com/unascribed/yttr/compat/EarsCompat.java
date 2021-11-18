package com.unascribed.yttr.compat;

import com.unascribed.ears.api.EarsAnchorPart;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

public class EarsCompat {

	public static void init() {
		EarsInhibitorRegistry.register("yttr", (part, peer) -> {
			PlayerEntity player = (PlayerEntity)peer;
			if (part.isAnchoredTo(EarsAnchorPart.HEAD)
					&& player.getEquippedStack(EquipmentSlot.HEAD).getItem() == YItems.SUIT_HELMET) {
				return true;
			}
			if ((part.isAnchoredTo(EarsAnchorPart.TORSO) || part.isAnchoredToAnyArm())
					&& player.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE) {
				return true;
			}
			if (part.isAnchoredToAnyLeg()
					&& player.getEquippedStack(EquipmentSlot.LEGS).getItem() == YItems.SUIT_LEGGINGS) {
				return true;
			}
			return false;
		});
	}
	
}
