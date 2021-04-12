package com.unascribed.yttr.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.EquipmentSlots;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.world.Geyser;

import com.google.common.base.Ascii;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

public class SuitScreen extends Screen {

	private boolean initialized = false;
	
	private int ticks = 0;
	private boolean hasDangered = false;
	
	private final SuitRenderer sr = new SuitRenderer();
	
	private final List<Geyser> geysers;
	
	private float posX, posZ;
	
	private boolean holdingForward;
	private boolean holdingLeft;
	private boolean holdingRight;
	private boolean holdingBack;
	
	private Geyser mouseOver;
	
	public SuitScreen(List<Geyser> geysers) {
		super(new LiteralText(""));
		this.geysers = Lists.newArrayList(geysers);
	}
	
	public void addGeyser(Geyser g) {
		geysers.add(g);
	}
	
	@Override
	protected void init() {
		if (!initialized) {
			initialized = true;
			client.getSoundManager().stopAll();
		}
		if (client.player != null) {
			sr.setColor(LampBlockItem.getColor(client.player.getEquippedStack(EquipmentSlot.HEAD)));
			posX = (float)client.player.getX();
			posZ = (float)client.player.getZ();
		}
	}
	
	@Override
	public void tick() {
		if (ticks == 1) client.getSoundManager().play(new SuitSound(YSounds.DIVE));
		ticks++;
		sr.tick();
		if (client.player != null) {
			client.player.setPos(client.player.getPos().x, -12, client.player.getPos().z);
			if (!Yttr.isWearingFullSuit(client.player)) {
				client.openScreen(null);
			}
		}
		
		float movementX = 0;
		float movementZ = 0;
		if (holdingForward) {
			movementZ++;
		}
		if (holdingBack) {
			movementZ--;
		}
		if (holdingLeft) {
			movementX++;
		}
		if (holdingRight) {
			movementX--;
		}
		float l = MathHelper.sqrt((movementX * movementX) + (movementZ * movementZ));
		if (l > 1e-5) {
			movementX /= l;
			movementZ /= l;
			posX += movementX*Yttr.DIVING_BLOCKS_PER_TICK;
			posZ += movementZ*Yttr.DIVING_BLOCKS_PER_TICK;
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.clearColor(0, 0, 0, 1);
		RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
		if (ticks < 40) return;
		sr.setUp();
		sr.drawText(matrices, "integrity", width-70, height-40, delta);
		sr.drawElement(matrices, "integrity-backdrop", width-96, height-28, 0, 30, 80, 8, delta);
		
		sr.drawText(matrices, "distance", 10, height-40, delta);
		int dist = 0;
		if (client.player != null) {
			dist = (int)MathHelper.sqrt(client.player.getPos().squaredDistanceTo(posX, client.player.getPos().y, posZ));
		}
		sr.drawText(matrices, "distance-num", Integer.toString(dist), 10, height-28, delta);
		
		float integrity = 1;
		if (client.player != null) {
			int dmg = 0;
			int integ = 0;
			for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
				ItemStack is = client.player.getEquippedStack(slot);
				if (is.getItem() instanceof SuitArmorItem) {
					SuitArmorItem sai = (SuitArmorItem)is.getItem();
					dmg += sai.getIntegrityDamage(is);
					integ += sai.getIntegrity(is);
				}
			}
			integrity = 1-(dmg/(float)integ);
		}
		
		sr.drawElement(matrices, "integrity-bar", width-96, height-28, 0, 38, (int)(80*integrity), 8, delta);
		int blinkSpeed = -1;
		if (integrity < 0.1f) {
			blinkSpeed = 3;
		} else if (integrity < 0.3f) {
			blinkSpeed = 6;
		} else if (integrity < 0.5f) {
			blinkSpeed = 12;
		}
		if (blinkSpeed != -1 && (ticks/blinkSpeed)%4 != 0) {
			if (!hasDangered) {
				hasDangered = true;
				client.getSoundManager().play(new SuitSound(YSounds.DANGER));
			}
			sr.drawElement(matrices, "warning", width-27, height-18, 0, 18, 11, 12, delta);
		} else {
			hasDangered = false;
		}
		
		
		int cX = width/2;
		int cY = height/2;
		
		mouseOver = null;
		for (Geyser g : geysers) {
			float dX = posX-g.pos.getX();
			float dZ = posZ-g.pos.getZ();
			int x = (int)(cX+(dX/50))-6;
			int y = (int)(cY+(dZ/50))-6;
			sr.drawElement(matrices, "geyser-"+g.id, x, y, 23, 18, 12, 12, delta);
			if (mouseX >= x && mouseX < x+12 &&
					mouseY >= y && mouseY < y+12) {
				mouseOver = g;
				int d = (int)Math.sqrt((dX * dX) + (dZ * dZ));
				sr.drawText(matrices, Ascii.toLowerCase(g.name), mouseX+10, mouseY+10, delta);
				sr.drawText(matrices, "geyser-"+g.id+"-dist", "distance "+d, mouseX+10, mouseY+22, delta);
			}
		}
		
		sr.drawElement(matrices, "map-border", (width-200)/2, (height-200)/2, 80, 0, 200, 200, delta);
		sr.tearDown();
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (mouseOver != null) {
				System.out.println(mouseOver);
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (client.options.keyForward.matchesKey(keyCode, scanCode)) {
			holdingForward = true;
		}
		if (client.options.keyLeft.matchesKey(keyCode, scanCode)) {
			holdingLeft = true;
		}
		if (client.options.keyRight.matchesKey(keyCode, scanCode)) {
			holdingRight = true;
		}
		if (client.options.keyBack.matchesKey(keyCode, scanCode)) {
			holdingBack = true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (client.options.keyForward.matchesKey(keyCode, scanCode)) {
			holdingForward = false;
		}
		if (client.options.keyLeft.matchesKey(keyCode, scanCode)) {
			holdingLeft = false;
		}
		if (client.options.keyRight.matchesKey(keyCode, scanCode)) {
			holdingRight = false;
		}
		if (client.options.keyBack.matchesKey(keyCode, scanCode)) {
			holdingBack = false;
		}
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	
}
