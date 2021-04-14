package com.unascribed.yttr.client.screen.handled;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.inventory.SuitStationScreenHandler;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.mechanics.SuitResource;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SuitStationScreen extends HandledScreen<SuitStationScreenHandler> {

	private static final Identifier BG = new Identifier("yttr", "textures/gui/suit_station.png");
	
	public SuitStationScreen(SuitStationScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		backgroundWidth = 201;
		backgroundHeight = 181;
		titleX = 32;
		playerInventoryTitleX = 32;
		playerInventoryTitleY = 86;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
		
		if (isEntireSuitPresent()) {
			client.getTextureManager().bindTexture(BG);
			LampColor color = LampBlockItem.getColor(handler.getSlot(0).getStack());
			RenderSystem.color3f(((color.glowColor>>16)&0xFF)/255f, ((color.glowColor>>8)&0xFF)/255f, (color.glowColor&0xFF)/255f);
			drawTexture(matrices, 129, 9, 231, 0, 16, 16, 256, 256);
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);
		client.getTextureManager().bindTexture(BG);
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		
		if (isEntireSuitPresent()) {
			ItemStack chest = handler.getSlot(1).getStack();
			SuitArmorItem sai = (SuitArmorItem)chest.getItem();
			int fuelH = (sai.getResourceAmount(chest, SuitResource.FUEL)*70)/SuitResource.FUEL.getMaximum();
			
			int fuelHR = (70-fuelH);
			
			int fuelFrames = 39;
			int frametime = 2;
			
			float t = (client.player.age+delta)/frametime;
			int fuelFrame = (int)((t/frametime)%fuelFrames);
			int nextFuelFrame;
			if (fuelFrame > 19) {
				fuelFrame = 19-(fuelFrame-20);
				nextFuelFrame = (fuelFrame-1)%fuelFrames;
				if (nextFuelFrame < 0) nextFuelFrame = 19;
			} else {
				nextFuelFrame = (fuelFrame+1)%fuelFrames;
			}
			float frameA = (t%frametime)/frametime;
			
			
			drawTexture(matrices, x+153, y+9+fuelHR, fuelFrame*10, 186+fuelHR, 10, fuelH, 256, 256);
			RenderSystem.enableAlphaTest();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.color4f(1, 1, 1, frameA);
			drawTexture(matrices, x+153, y+9+fuelHR, nextFuelFrame*10, 186+fuelHR, 10, fuelH, 256, 256);
			RenderSystem.disableBlend();
			RenderSystem.disableAlphaTest();
			RenderSystem.enableAlphaTest();
			RenderSystem.color4f(1, 1, 1, 1);
			
			int oxyH = (sai.getResourceAmount(chest, SuitResource.OXYGEN)*68)/SuitResource.OXYGEN.getMaximum();
			int oxyHR = (70-oxyH);
			drawTexture(matrices, x+168, y+9+oxyH, 211, 0, 10, oxyHR, 256, 256);
			
			
			int integH = (sai.getResourceAmount(chest, SuitResource.INTEGRITY)*70)/SuitResource.INTEGRITY.getMaximum();
			int integHR = (70-integH);
			
			drawTexture(matrices, x+183, y+9+integHR, 221, integHR, 10, integH, 256, 256);
			drawTexture(matrices, x+183, y+9+integHR, 221, 0, 10, Math.min(integH, 4), 256, 256);
			
			drawTexture(matrices, x+153, y+9, 201, 0, 10, 70, 256, 256);
			drawTexture(matrices, x+168, y+9, 201, 0, 10, 70, 256, 256);
		} else {
			drawTexture(matrices, x+152, y+8, 201, 84, 12, 72, 256, 256);
			drawTexture(matrices, x+167, y+8, 201, 84, 12, 72, 256, 256);
			drawTexture(matrices, x+182, y+8, 201, 84, 12, 72, 256, 256);
		}
		
		int burnH = (handler.getFuelTime()*14)/handler.getMaxFuelTime();
		int burnRH = 14-burnH;
		drawTexture(matrices, x+33, y+46+burnRH, 201, 70+burnRH, 14, burnH, 256, 256);
		
		int fluxH = (handler.getFluxLeft()*14)/handler.getMaxFluxLeft();
		int fluxRH = 14-fluxH;
		drawTexture(matrices, x+78, y+46+fluxRH, 215, 70+fluxRH, 14, fluxH, 256, 256);
	}
	
	private boolean isEntireSuitPresent() {
		for (int i = 0; i < 4; i++) {
			if (!(handler.getSlot(i).getStack().getItem() instanceof SuitArmorItem)) {
				return false;
			}
		}
		return true;
	}
	
}
