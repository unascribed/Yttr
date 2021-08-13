package com.unascribed.yttr.client.suit;

import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.util.math.Interp;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.hash.Hashing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SuitRenderer {

	public static final Identifier SUIT_TEX = new Identifier("yttr", "textures/gui/suit.png");
	public static final int SUIT_TEX_WIDTH = 280;
	public static final int SUIT_TEX_HEIGHT = 200;

	private final Multiset<String> suitElementTickTimes = HashMultiset.create();
	private final Map<String, Integer> uniqifiers = Maps.newHashMap();
	private final Random rand = new Random();
	private final Random independentRand = new Random();
	private int seed = Long.hashCode(System.nanoTime());
	
	private float r = 1;
	private float g = 1;
	private float b = 1;
	
	public void setColor(LampColor color) {
		setColor(color.glowColor);
	}
	
	public void setColor(int color) {
		setColor(
			((color>>16)&0xFF)/255f,
			((color>>8)&0xFF)/255f,
			(color&0xFF)/255f
		);
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r*0.65f;
		this.g = g*0.65f;
		this.b = b*0.65f;
	}
	
	public void setUp() {
		// Desyncing the state manager for fun and profit
		RenderSystem.enableAlphaTest();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
		MinecraftClient.getInstance().getTextureManager().bindTexture(SuitRenderer.SUIT_TEX);
	}
	
	public void tearDown() {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableAlphaTest();
	}
	
	public void tick() {
		for (String k : suitElementTickTimes.elementSet()) {
			suitElementTickTimes.add(k);
		}
	}
	
	public void drawText(MatrixStack matrices, String s, int x, int y, float delta) {
		drawText(matrices, s, s, x, y, delta);
	}
	
	public void drawText(MatrixStack matrices, String id, String s, int x, int y, float delta) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int u, v;
			if (c >= 'a' && c <= 'z') {
				int l = c-'a';
				u = (l%13)*5;
				v = (l/13)*9;
			} else if (c >= '0' && c <= '?') {
				int l = c-'0';
				u = l*5;
				v = 46;
			} else if (c >= '!' && c <= '/') {
				int l = c-'!';
				u = l*5;
				v = 82;
			} else if (c == ' ') {
				x += 6;
				continue;
			} else {
				u = 75;
				v = 46;
			}
			drawElement(matrices, id+":"+i, x, y, u, v, 5, 9, delta);
			x += 6;
		}
	}
	
	public void drawElement(MatrixStack matrices, String name, int x, int y, int u, int v, int w, int h, float delta) {
		int uniq = uniqifiers.computeIfAbsent(name, k -> Hashing.murmur3_32(seed).hashUnencodedChars(name).asInt());
		rand.setSeed(uniq);
		int timeToEnable = rand.nextInt(10);
		
		if (!suitElementTickTimes.contains(name)) suitElementTickTimes.add(name);
		float t = (suitElementTickTimes.count(name)-1)+delta;
		if (t < timeToEnable) return;
		t -= timeToEnable;
		
		int timeToFullBrightness = rand.nextInt(20)+5;
		int flickeriness = rand.nextInt(10);
		
		float a;
		if (t < timeToFullBrightness) {
			a = Interp.sCurve5(t/timeToFullBrightness);
		} else {
			a = 1;
		}
		float wA = w > 10 && h < 10 ? a : 1;
		float hA = h > 10 ? a : 1;
		a = 0.3f+(a*0.7f);
		
		if (flickeriness > 0 && independentRand.nextInt(flickeriness*8) == 0) {
			a *= 0.85f;
		}

		RenderSystem.color4f(r, g, b, a * 0.1f);
		for (int xo = -1; xo <= 1; xo++) {
			for (int yo = -1; yo <= 1; yo++) {
				DrawableHelper.drawTexture(matrices, x+xo, y+yo, u, v, (int)(w*wA), (int)(h*hA), SuitRenderer.SUIT_TEX_WIDTH, SuitRenderer.SUIT_TEX_HEIGHT);
			}
		}
		RenderSystem.color4f(r, g, b, a);
		DrawableHelper.drawTexture(matrices, x, y, u, v, (int)(w*wA), (int)(h*hA), SuitRenderer.SUIT_TEX_WIDTH, SuitRenderer.SUIT_TEX_HEIGHT);
	}

	public void drawBar(MatrixStack matrices, String id, int x, int y, float value, boolean flip, float delta) {
		int w = (int)(80*value);
		int xo = (flip?80-w:0);
		drawElement(matrices, id+"-backdrop", x, y, 0, 30, 80, 8, delta);
		drawElement(matrices, id+"-bar", x+xo, y, xo, 38, w, 8, delta);
	}

}
