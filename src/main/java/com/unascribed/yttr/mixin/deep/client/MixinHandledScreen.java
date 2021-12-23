package com.unascribed.yttr.mixin.deep.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.screen.handled.DSUScreen;
import com.unascribed.yttr.init.YTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class MixinHandledScreen {

	private static final Identifier YTTR$TINYNUMBERS = new Identifier("yttr", "textures/gui/tiny_numbers.png");
	
	private ItemStack yttr$storedStack;
	
	@Inject(at=@At("HEAD"), method="drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V")
	public void drawSlotHead(MatrixStack matrices, Slot slot, CallbackInfo ci) {
		Object self = this;
		if (self instanceof DSUScreen && slot.getMaxItemCount() == 4096 && slot.getStack().getItem().isIn(YTags.Item.DSU_HIGHSTACK) && slot.getStack().getCount() > 1) {
			yttr$storedStack = slot.getStack();
			ItemStack copy = yttr$storedStack.copy();
			copy.setCount(1);
			slot.setStack(copy);
		}
	}
	
	@Inject(at=@At("TAIL"), method="drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V")
	public void drawSlotTail(MatrixStack matrices, Slot slot, CallbackInfo ci) {
		if (yttr$storedStack != null) {
			ItemStack stack = yttr$storedStack;
			slot.setStack(stack);
			yttr$storedStack = null;
			MinecraftClient.getInstance().getTextureManager().bindTexture(YTTR$TINYNUMBERS);
			String str = Integer.toString(stack.getCount());
			int w = str.length()*4;
			for (int p = 0; p < 2; p++) {
				int x = slot.x+18-w;
				int y = slot.y+16-4;
				if (p == 0) {
					RenderSystem.color3f(0.25f, 0.25f, 0.25f);
				} else {
					RenderSystem.color3f(1, 1, 1);
					x--;
					y--;
				}
				for (int i = 0; i < str.length(); i++) {
					int j = str.charAt(i)-'0';
					int u = (j%5)*3;
					int v = (j/5)*5;
					DrawableHelper.drawTexture(matrices, x, y, 300, u, v, 3, 5, 10, 15);
					x += 4;
				}
			}
			RenderSystem.color3f(1, 1, 1);
		}
	}
	
}
