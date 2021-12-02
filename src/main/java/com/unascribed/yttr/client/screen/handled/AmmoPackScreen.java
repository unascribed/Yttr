package com.unascribed.yttr.client.screen.handled;

import com.unascribed.yttr.inventory.AmmoPackScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AmmoPackScreen extends HandledScreen<AmmoPackScreenHandler> {

	private static final Identifier BG = new Identifier("yttr", "textures/gui/ammo_pack.png");
	
	public AmmoPackScreen(AmmoPackScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 163;
		playerInventoryTitleY = 70;
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
		
		for (Slot s : handler.slots) {
			if (s.inventory != playerInventory && !s.hasStack()) {
				drawTexture(matrices, x+s.x, y+s.y, 176, 0, 16, 16, 256, 256);
			}
		}
		
	}
	
}
