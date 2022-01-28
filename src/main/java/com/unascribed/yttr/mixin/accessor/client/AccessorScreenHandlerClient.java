package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.screen.ScreenHandler;

@Mixin(ScreenHandler.class)
public interface AccessorScreenHandlerClient {

	@Accessor("actionId")
	short yttr$getActionId();
	
}
