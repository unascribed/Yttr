package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.screen.handled.RafterScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class MixinHandledScreen {

	@Inject(at=@At("RETURN"), method="getSlotAt", cancellable=true)
	private void getSlotAt(double x, double y, CallbackInfoReturnable<Slot> ci) {
		Object self = this;
		if (ci.getReturnValue() == null && self instanceof RafterScreen) {
			ci.setReturnValue(((RafterScreen)self).getAltSlotAt(x, y));
		}
	}
	
}
