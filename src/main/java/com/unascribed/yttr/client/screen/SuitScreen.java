package com.unascribed.yttr.client.screen;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.suit.SuitMusic;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.client.suit.SuitSound;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.network.MessageC2SDivePos;
import com.unascribed.yttr.network.MessageC2SDiveTo;
import com.unascribed.yttr.world.Geyser;

import com.google.common.base.Ascii;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

public class SuitScreen extends Screen {

	private boolean initialized = false;
	
	private int ticks = 0;
	private boolean hasDangered = false;
	
	private final SuitRenderer sr = new SuitRenderer();
	
	private final List<Geyser> geysers;
	
	private float posX, posZ;
	private int pressure = 120;
	private int pressureLag = 120;
	private int lastPressureLag = 120;
	
	private boolean fastDiving = false;
	private Multiset<SuitResource> fdResourceCosts = EnumMultiset.create(SuitResource.class);
	private float fdStartX, fdStartZ;
	private float fdTgtX, fdTgtZ;
	private int fastDiveTime;
	private int fastDiveTicks;
	
	private boolean holdingForward;
	private boolean holdingLeft;
	private boolean holdingRight;
	private boolean holdingBack;
	
	private Geyser mouseOver;
	private double mouseOverDist;
	
	private int errorId;
	private int errorTicks;
	private String error;
	
	private SuitSound music;
	
	private float zoom = 1;
	
	public SuitScreen(int x, int z, List<Geyser> geysers) {
		super(new LiteralText(""));
		posX = x+0.5f;
		posZ = z+0.5f;
		this.geysers = Lists.newArrayList(geysers);
	}
	
	public void addGeyser(Geyser g) {
		geysers.add(g);
	}
	
	public void setPos(int x, int z) {
		posX = x+0.5f;
		posZ = z+0.5f;
	}
	
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}
	
	public void showError(String msg) {
		errorId++;
		errorTicks = 0;
		error = msg;
	}

	public void startFastDive(Multiset<SuitResource> costs, int x, int z, int time) {
		fastDiving = true;
		fdResourceCosts.clear();
		fdResourceCosts.addAll(costs);
		fdStartX = posX;
		fdStartZ = posZ;
		fdTgtX = x+0.5f;
		fdTgtZ = z+0.5f;
		fastDiveTime = time;
	}
	
	@Override
	protected void init() {
		if (!initialized) {
			initialized = true;
			client.getSoundManager().stopAll();
		}
		if (client.player != null) {
			sr.setColor(LampBlockItem.getColor(client.player.getEquippedStack(EquipmentSlot.HEAD)));
		}
	}
	
	@Override
	public void tick() {
		if (ticks == 1) client.getSoundManager().play(new SuitSound(YSounds.DIVE));
		ticks++;
		if (ticks > 20*20) {
			if (music == null) {
				music = new SuitMusic(YSounds.VOID_MUSIC, 0.5f, SoundCategory.AMBIENT);
			}
			int time = ticks-(20*20);
			music.setVolume(MathHelper.clamp(time/(20*20f), 0.05f, 0.5f));
			if (!client.getSoundManager().isPlaying(music)) {
				client.getSoundManager().play(music);
			}
		}
		if (!fastDiving && ticks % 5 == 0) {
			new MessageC2SDivePos((int)posX, (int)posZ).sendToServer();
		}
		if (fastDiving) {
			fastDiveTicks++;
		}
		sr.tick();
		if (client.player != null) {
			client.player.setPos(client.player.getPos().x, -12, client.player.getPos().z);
			client.player.fallDistance = 0;
			if (!Yttr.isWearingFullSuit(client.player)) {
				client.openScreen(null);
			}
		}
		if (!fastDiving) {
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
				int moveSpeed = Yttr.DIVING_BLOCKS_PER_TICK;
				ItemStack is = client.player.getEquippedStack(EquipmentSlot.CHEST);
				if (is.getItem() instanceof SuitArmorItem) {
					SuitArmorItem sai = (SuitArmorItem)is.getItem();
					for (SuitResource sr : SuitResource.VALUES) {
						moveSpeed /= sr.getSpeedDivider(sai.getResourceAmount(is, sr) <= 0);
					}
				}
				posX += movementX*moveSpeed;
				posZ += movementZ*moveSpeed;
			}
		}
		if (fastDiving || holdingForward || holdingBack || holdingLeft || holdingRight) {
			if (ThreadLocalRandom.current().nextInt(fastDiving ? 10 : 20) == 0) {
				client.getSoundManager().play(new SuitSound(YSounds.DIVE_THRUST, 0.2f));
			}
		}

		lastPressureLag = pressureLag;
		if (pressure != pressureLag) {
			if (Math.abs(pressure-pressureLag) < 3) {
				pressureLag = pressure;
			} else {
				pressureLag += (pressure-pressureLag)/3;
			}
		}
		
		if (error != null) {
			if (errorTicks++ > 80) {
				error = null;
			}
		}
	}
	
	private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), (floatBuffer) -> {
		Untracker.untrack(MemoryUtil.memAddress(floatBuffer));
	});
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.clearColor(0, 0, 0, 1);
		RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
		if (ticks < 40) return;
		float factor = 0;
//		if (ticks % 100 < 40) {
//			float t = (ticks+delta)%100;
//			factor = MathHelper.sin((float) (t/20f*Math.PI));
//		}
		factor *= (client.options.distortionEffectScale+0.2f);
		factor /= 2;
		if (factor > 0 || factor < 0) {
			float t = ticks+delta;
			RenderSystem.matrixMode(GL11.GL_PROJECTION);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(width/2+((MathHelper.sin(t/2)*20)*factor), height/2, 0);
			RenderSystem.rotatef((MathHelper.sin(t)*30)*(factor/4), 0, 0, 1);
			if (factor > 0.3f || factor < -0.3f) {
				float sig = Math.signum(factor);
				float f = (Math.abs(factor)-0.3f)*sig;
				MATRIX_BUFFER.clear();
				MATRIX_BUFFER.put(1).put(MathHelper.cos(t*4)/8*f).put(0).put(0);
				MATRIX_BUFFER.put(MathHelper.sin(t)/2*f).put(1).put(0).put(0);
				MATRIX_BUFFER.put(0).put(0).put(1).put(0);
				MATRIX_BUFFER.put(0).put(0).put(0).put(1);
				MATRIX_BUFFER.flip();
				GlStateManager.multMatrix(MATRIX_BUFFER);
			}
			RenderSystem.translatef(-width/2, -height/2, 0);
			RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		}
		sr.setUp();
		sr.drawText(matrices, "distance", 10, height-78, delta);
		int dist = 0;
		if (client.player != null) {
			dist = (int)MathHelper.sqrt(client.player.getPos().squaredDistanceTo(posX, client.player.getPos().y, posZ));
		}
		sr.drawText(matrices, "distance-num", dist+"m", 10, height-66, delta);
		
		float fastDiveT = fastDiving ? (fastDiveTicks+delta)/fastDiveTime : 0;
		
		if (fastDiving) {
			posX = fdStartX+((fdTgtX-fdStartX)*fastDiveT);
			posZ = fdStartZ+((fdTgtZ-fdStartZ)*fastDiveT);
		}
		
		float pressureA = (lastPressureLag+((pressureLag-lastPressureLag)*delta))/1000f;
		
		sr.drawText(matrices, "pressure", 10, height-46, delta);
		sr.drawElement(matrices, "pressure-notches", 10, height-34, 0, 55, 81, 8, delta);
		if (!fastDiving) {
			sr.drawElement(matrices, "pressure-indicator", 9+((int)(pressureA*80)), height-25, 0, 63, 3, 6, delta);
			sr.drawText(matrices, "pressure-num", pressureLag+"kpa", 10, height-16, delta);
		}
		
		ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
		if (chest.getItem() instanceof SuitArmorItem) {
			Multiset<SuitResource> costs = null;
			if (mouseOver != null) {
				costs = Yttr.determineNeededResourcesForFastDive(mouseOverDist);
			}
			SuitArmorItem sai = (SuitArmorItem)chest.getItem();
			int resourceBarY = (height-20)-(SuitResource.VALUES.size()*24);
			float lowestResource = 1;
			for (SuitResource res : SuitResource.VALUES) {
				int len = res.name().length()*6;
				
				String name = res.name().toLowerCase(Locale.ROOT);
				
				float amt = sai.getResourceAmount(chest, res);
				if (fastDiving && fdResourceCosts.contains(res)) {
					amt += fdResourceCosts.count(res)*(1-fastDiveT);
				}
				
				float a = amt/res.getMaximum();
				lowestResource = Math.min(a, lowestResource);
				
				if (a < 0.5f) {
					sr.drawElement(matrices, name+"-warning", width-96, resourceBarY-2, 0, 18, 11, 12, delta);
				}
				
				sr.drawText(matrices, name, width-len-16, resourceBarY, delta);
				sr.drawBar(matrices, name, width-96, resourceBarY+12, a, true, delta);
				if (costs != null) {
					int mainW = (int)(80*a);
					int xo = 80-mainW;
					float ca = costs.count(res)/(float)res.getMaximum();
					RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ZERO, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
					sr.drawElement(matrices, name+"-bar-cut", (width-97)+xo+2, resourceBarY+14, xo+2, 76, (int)(80*ca), 4, delta);
					RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
				}
				resourceBarY += 24;
			}
			int blinkSpeed = -1;
			if (lowestResource < 0.1f) {
				blinkSpeed = 3;
			} else if (lowestResource < 0.3f) {
				blinkSpeed = 6;
			} else if (lowestResource < 0.5f) {
				blinkSpeed = 12;
			}
			if (blinkSpeed != -1 && (ticks/blinkSpeed)%4 != 0) {
				if (!hasDangered && !client.player.isCreative()) {
					hasDangered = true;
					client.getSoundManager().play(new SuitSound(YSounds.DANGER));
				}
				sr.drawElement(matrices, "warning", width-27, height-18, 0, 18, 11, 12, delta);
			} else {
				hasDangered = false;
			}
		}
		
		
		int cX = width/2;
		int cY = height/2;
		
		sr.drawText(matrices, "n", cX-2, cY-95, delta);
		sr.drawText(matrices, "e", cX+90, cY-4, delta);
		sr.drawText(matrices, "s", cX-2, cY+83, delta);
		sr.drawText(matrices, "w", cX-95, cY-4, delta);
		
		int scale = (int)(25*zoom);
		
		mouseOver = null;
		for (Geyser g : geysers) {
			float dX = posX-g.pos.getX();
			float dZ = posZ-g.pos.getZ();
			int x = (int)(cX+(dX/scale))-6;
			int y = (int)(cY+(dZ/scale))-6;
			if (x < cX-100 || x > cX+90 || y < cY-95 || y > cY+90) continue;
			sr.drawElement(matrices, "geyser-"+g.id, x, y, 23, 18, 12, 12, delta);
			if (!fastDiving && mouseX >= x && mouseX < x+12 &&
					mouseY >= y && mouseY < y+12) {
				mouseOver = g;
				double d = Math.sqrt((dX * dX) + (dZ * dZ));
				mouseOverDist = d;
				sr.drawText(matrices, Ascii.toLowerCase(g.name), mouseX+10, mouseY+10, delta);
				sr.drawText(matrices, "geyser-"+g.id+"-dist", "distance "+((int)d), mouseX+10, mouseY+22, delta);
			}
		}
		
		int cornerX = (width-200)/2;
		int cornerY = (height-200)/2;
		sr.drawElement(matrices, "map-border", cornerX, cornerY, 80, 0, 200, 200, delta);
		sr.drawElement(matrices, "scale-indicator", cornerX+160, cornerY+185, 0, 69, 32, 6, delta);
		int scaleMeter = scale*32;
		String indicator;
		if (scaleMeter > 1000) {
			indicator = (scaleMeter/1000)+"."+((scaleMeter/100)%10)+"km";
		} else {
			indicator = scaleMeter+"m";
		}
		sr.drawText(matrices, "scale-indicator-num", indicator, cornerX+160, cornerY+173, delta);
		
		if (fastDiving) {
			sr.drawText(matrices, "navigating", 8, 8, delta);
		}
		
		if (error != null) {
			sr.drawText(matrices, "error-"+errorId, error, (width-(error.length()*6))/2, 8, delta);
		}
		
		sr.tearDown();
		if (factor > 0 || factor < 0) {
			RenderSystem.matrixMode(GL11.GL_PROJECTION);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		}
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
				new MessageC2SDiveTo(mouseOver.id).sendToServer();
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount < 0) {
			if (zoom < 0.2f) {
				if (zoom+0.01f > 0.2f) {
					zoom = 0.2f;
				} else {
					zoom += 0.01f;
				}
			} else {
				zoom += 0.1f;
			}
			if (zoom > 4) zoom = 4;
		}
		if (amount > 0) {
			if (zoom <= 0.2f) {
				zoom -= 0.01f;
			} else {
				zoom -= 0.1f;
			}
			if (zoom < 0.05f) zoom = 0.05f;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
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
