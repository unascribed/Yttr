package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.inventory.MagtankScreenHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MagtankScreen extends HandledScreen<MagtankScreenHandler> {

	private static final Identifier BG = new Identifier("yttr", "textures/gui/magtank.png");
	
	public MagtankScreen(MagtankScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 194;
		playerInventoryTitleY = backgroundHeight - 94;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		
		client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		for (int xo = 0; xo < 2; xo++) {
			for (int yo = 0; yo < 2; yo++) {
				drawSprite(matrices, x+4+(xo*32), y+14+(yo*32), 0, 32, 32, FluidRenderHandlerRegistry.INSTANCE.get(YFluids.VOID).getFluidSprites(null, null, YFluids.VOID.getDefaultState())[0]);
			}
		}
		
		client.getTextureManager().bindTexture(BG);
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
	}
	
}
