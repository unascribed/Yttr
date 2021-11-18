package com.unascribed.yttr.client.screen.handled;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VoidFilterScreen extends HandledScreen<VoidFilterScreenHandler> {

	private static final Identifier BG = new Identifier("yttr", "textures/gui/void_filter.png");
	
	private final List<GuiParticle> particles = Lists.newArrayList();
	
	public VoidFilterScreen(VoidFilterScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
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
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableAlphaTest();
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		if (!handler.isIndependent()) {
			drawTexture(matrices, x+115, y+3, 0, 189, 54, 67, 256, 256);
		}
		RenderSystem.disableBlend();
		
		matrices.push();
		matrices.translate(x, y, 0);
		for (GuiParticle gp : particles) {
			gp.render(matrices, delta);
		}
		matrices.pop();
		
		drawTexture(matrices, x+67, y+27, 176, 0, ((handler.getProgress()*40)/handler.getMaxProgress()), 28, 256, 256);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1, 1, 1, 0.75f);
		drawTexture(matrices, x+titleX-2, y+titleY-2, titleX-2, titleY-2, textRenderer.getWidth(getTitle())+4, 12, 256, 256);
		RenderSystem.disableBlend();
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
	}
	
	@Override
	public void tick() {
		Iterator<GuiParticle> iter = particles.iterator();
		while (iter.hasNext()) {
			GuiParticle gp = iter.next();
			if (gp.expired) {
				iter.remove();
			} else {
				gp.update();
			}
		}
		if (GuiParticle.rand.nextInt(4) == 0) {
			GuiParticle gp = new GuiParticle(32+((GuiParticle.rand.nextFloat()-GuiParticle.rand.nextFloat())*8), 37+((GuiParticle.rand.nextFloat()-GuiParticle.rand.nextFloat())*8));
			gp.gravity = -3+(GuiParticle.rand.nextFloat());
			gp.motionX = GuiParticle.rand.nextGaussian()/2;
			particles.add(gp);
		}
	}
	
	private static class GuiParticle {

		protected static final Random rand = new Random();
		
		public double prevPosX;
		public double prevPosY;

		public double posX;
		public double posY;

		public double motionX;
		public double motionY;

		public boolean expired;

		public int age;
		public int maxAge;
		public float gravity;

		public GuiParticle(double x, double y) {
			posX = x;
			posY = y;

			prevPosX = x;
			prevPosY = y;

			maxAge = (int)(8 / (rand.nextDouble() * 0.8 + 0.2))*2;
			age = 0;
			
			motionX = rand.nextFloat()-0.5f;
			motionY = rand.nextFloat()-0.5f;

			gravity = 12;
		}

		public void setExpired() {
			expired = true;
		}

		public void update() {
			prevPosX = posX;
			prevPosY = posY;

			if (age++ >= maxAge) {
				setExpired();
			}

			motionY += 0.04 * gravity;
			motionX *= 0.98;
			motionY *= 0.98;

			posX += motionX;
			posY += motionY;
		}

		public void render(MatrixStack matrices, float delta) {
			double interpPosX = prevPosX + (posX - prevPosX) * delta;
			double interpPosY = prevPosY + (posY - prevPosY) * delta;

			MinecraftClient.getInstance().getTextureManager().bindTexture(BG);
			float u = (7 - age * 8 / maxAge)*8;
			float v = 166;
			DrawableHelper.drawTexture(matrices, (int)interpPosX-4, (int)interpPosY-4, 0, u, v, 8, 8, 256, 256);
		}

	}
	
}
