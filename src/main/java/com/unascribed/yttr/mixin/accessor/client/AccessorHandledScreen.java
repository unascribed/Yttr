package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public interface AccessorHandledScreen {

	@Invoker("drawSlot")
	void yttr$drawSlot(MatrixStack matrices, Slot slot);
	
}
