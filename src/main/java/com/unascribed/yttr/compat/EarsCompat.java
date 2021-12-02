package com.unascribed.yttr.compat;

import com.unascribed.ears.api.EarsAnchorPart;
import com.unascribed.ears.api.EarsStateType;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.api.registry.EarsStateOverriderRegistry;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

public class EarsCompat {

	public static void init() {
		EarsInhibitorRegistry.register("yttr", (part, peer) -> {
			PlayerEntity player = (PlayerEntity)peer;
			if (part.isAnchoredTo(EarsAnchorPart.HEAD)
					&& player.getEquippedStack(EquipmentSlot.HEAD).getItem() == YItems.SUIT_HELMET
					&& EarsStateOverriderRegistry.isActive(EarsStateType.WEARING_HELMET, peer, true).getValue()) {
				return true;
			}
			if ((part.isAnchoredTo(EarsAnchorPart.TORSO) || part.isAnchoredToAnyArm())
					&& player.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE
					&& EarsStateOverriderRegistry.isActive(EarsStateType.WEARING_CHESTPLATE, peer, true).getValue()) {
				return true;
			}
			if (part.isAnchoredToAnyLeg()
					&& player.getEquippedStack(EquipmentSlot.LEGS).getItem() == YItems.SUIT_LEGGINGS
					&& EarsStateOverriderRegistry.isActive(EarsStateType.WEARING_LEGGINGS, peer, true).getValue()) {
				return true;
			}
			return false;
		});
		
		Yttr.isVisuallyWearingBoots = pe -> EarsStateOverriderRegistry.isActive(EarsStateType.WEARING_BOOTS, pe, !pe.getEquippedStack(EquipmentSlot.FEET).isEmpty()).getValue();
	}
	
}
