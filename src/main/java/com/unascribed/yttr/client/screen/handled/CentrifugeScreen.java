package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CentrifugeScreen extends HandledScreen<CentrifugeScreenHandler> {

	private static final Identifier BG = new Identifier("yttr", "textures/gui/centrifuge.png");
	
	public CentrifugeScreen(CentrifugeScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 201;
		playerInventoryTitleY = 107;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		client.getTextureManager().bindTexture(BG);
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		float prog = handler.getSpinTime()/(float)handler.getMaxSpinTime();
		int p = Math.round(41*prog);
		int pr = 41-p;
		int a = p-37;
		int ar = 4-a;
		if (p > 36) {
			drawShadowedTexture(matrices, x+69, y+19, 176, 41+(a*9), 9, 9, 256, 256);
			drawShadowedTexture(matrices, x+52, y+66, 203, 41+(a*9), 9, 9, 256, 256);
		}
		drawShadowedTexture(matrices, x+68, y+19+pr, 176, pr, 10, p, 256, 256);
		drawShadowedTexture(matrices, x+98, y+51, 186, 0, 10, p, 256, 256);
		drawShadowedTexture(matrices, x+84, y+35, 196, 0, p, 11, 256, 256);
		drawShadowedTexture(matrices, x+51+pr, y+65, 196+pr, 11, p, 11, 256, 256);
		if (p > 36) {
			drawShadowedTexture(matrices, x+98, y+82, 185, 41+(ar*9), 9, 9, 256, 256);
			drawShadowedTexture(matrices, x+115, y+36, 194, 41+(ar*9), 9, 9, 256, 256);
		}
		float fuel = handler.getFuelTime()/(float)handler.getMaxFuelTime();
		int h = (int)Math.ceil(fuel*14);
		int ih = 14-h;
		drawTexture(matrices, x+8, y+69+ih, 237, ih, 14, h, 256, 256);
	}

	private void drawShadowedTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		RenderSystem.color4f(0.4f, 0.4f, 0.4f, 1);
		drawTexture(matrices, x, y+1, u, v, width, height, textureWidth, textureHeight);
		RenderSystem.color4f(1, 1, 1, 1);
		drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
	}
	
}
