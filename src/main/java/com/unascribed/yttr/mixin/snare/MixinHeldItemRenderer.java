package com.unascribed.yttr.mixin.snare;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Shadow private ItemStack mainHand;
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "updateHeldItems")
    private void updateHeldItems(CallbackInfo ci) {
        ItemStack stack = client.player.getMainHandStack();
        if (this.mainHand.getItem() == YItems.SNARE && stack.getItem() == YItems.SNARE) {
            mainHand = stack;
        }
    }
}
