package com.unascribed.yttr.client;

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
		float prog = (System.currentTimeMillis()%2000)/2000f;
		int p = (int)Math.ceil(41*prog);
		int pr = 41-p;
		drawTexture(matrices, x+68, y+19+pr, 176, pr, 10, p, 256, 256);
		drawTexture(matrices, x+98, y+51, 186, 0, 10, p, 256, 256);
		drawTexture(matrices, x+84, y+35, 196, 0, p, 11, 256, 256);
		drawTexture(matrices, x+51+pr, y+65, 196+pr, 11, p, 11, 256, 256);
		float fuel = 1-(System.currentTimeMillis()%8000)/8000f;
		int h = (int)Math.ceil(fuel*14);
		int ih = 14-h;
		drawTexture(matrices, x+8, y+69+ih, 237, ih, 14, h, 256, 256);
	}
	
}
