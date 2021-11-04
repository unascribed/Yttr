package com.unascribed.yttr.mixin.diving.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.item.SuitArmorItem;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;

@Mixin(targets="com.unascribed.ears.common.render.AbstractEarsRenderDelegate")
@Pseudo
public class MixinAbstractEarsRenderDelegate {

	@Shadow(remap=false)
	private int skipRendering;
	@Shadow(remap=false)
	protected Object peer;
	
	@Inject(at=@At("HEAD"), method="anchorTo", cancellable=true, remap=false)
	public void anchorTo(@Coerce Enum<?> part, CallbackInfo ci) {
		if (peer == null || !(peer instanceof AbstractClientPlayerEntity)) return;
		String name = part.name();
		EquipmentSlot slot;
		switch (name) {
			case "HEAD": slot = EquipmentSlot.HEAD; break;
			case "TORSO": case "LEFT_ARM": case "RIGHT_ARM": slot = EquipmentSlot.CHEST; break;
			case "LEFT_LEG": case "RIGHT_LEG": slot = EquipmentSlot.LEGS; break;
			default: return;
		}
		if (((AbstractClientPlayerEntity)peer).getEquippedStack(slot).getItem() instanceof SuitArmorItem) {
			if (skipRendering == 0) skipRendering = 1;
			ci.cancel();
		}
	}
	
	
}
