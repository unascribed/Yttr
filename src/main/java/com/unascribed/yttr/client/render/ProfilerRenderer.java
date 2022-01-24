package com.unascribed.yttr.client.render;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.mixin.debug.AccessorMinecraftClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;

public class ProfilerRenderer {

	public static boolean enabled = false;
	
	private static String selected = "root";
	private static int cursorIndex;
	private static boolean stepIn;
	private static boolean stepOut;
	
	public static void render(MatrixStack ms) {
		if (enabled) {
			MinecraftClient.getInstance().getProfiler().swap("yttr:profiler");
			ms.push();
			int w = MinecraftClient.getInstance().getWindow().getScaledWidth();
			int h = MinecraftClient.getInstance().getWindow().getScaledHeight();
			float f = (float)MinecraftClient.getInstance().getWindow().getScaleFactor();
			float fr = 2/f;
			ms.scale(fr, fr, 1);
			w /= fr;
			h /= fr;
			ProfileResult res = ((AccessorMinecraftClient)MinecraftClient.getInstance()).yttr$getTickProfilerResult();
			TextRenderer tr = MinecraftClient.getInstance().textRenderer;
			if (res != null) {
				int y = 0;
				int i = 0;
				if (cursorIndex < 0) cursorIndex = 0;
				String hilight = null;
				if (stepOut && selected.contains("\u001E")) {
					int idx = selected.lastIndexOf('\u001E');
					hilight = selected.substring(idx+1);
					selected = selected.substring(0, idx);
					cursorIndex = -5;
				}
				String sel = selected;
				boolean overflowTop = false;
				if (cursorIndex > (h-100)/18) {
					overflowTop = true;
					y += (((h-100)/18)-cursorIndex)*18;
				}
				for (ProfilerTiming pt : res.getTimings(sel)) {
					int rw = i == 0 ? 200 : 150;
					if (stepOut && pt.name.equals(hilight)) {
						cursorIndex = i-1;
					}
					if (i-1 == cursorIndex) {
						rw = 160;
						if (stepIn) {
							selected += "\u001E"+pt.name;
							cursorIndex = 0;
							stepIn = false;
						}
					}
					int bw = (int)(pt.parentSectionUsagePercentage*1.5);
					int bw2 = (int)(pt.totalUsagePercentage*1.5);
					String hash = sel+"\u001E"+pt.name;
					String path = "";
					String name = pt.name;
					if (name.contains("\u001E")) {
						int idx = name.lastIndexOf('\u001E');
						path = name.substring(0, idx+1).replace('\u001E', '/');
						name = name.substring(idx+1);
						hash = sel;
					}
					int fontColor = 0;
					if ("unspecified".equals(name)) {
						drawBox(ms, w-rw, y, rw, 18, 0xFF005500);
						fontColor = -1;
					} else {
						drawHashBox(ms, w-rw, y, rw, 18, hash);
					}
					drawBox(ms, w-bw, y+16, bw, 2, -1);
					drawBox(ms, w-bw2, y+16, bw2, 2, 0xFF000000);
					drawRightAligned(tr, ms, name, w-2, y+2, fontColor, true);
					if (i-1 == cursorIndex) {
						for (int j = 0; j < 4; j++) {
							tr.draw(ms, ">", w-158+j, y+6, 0);
						}
					}
					ms.push();
						ms.translate(w, y, 0);
						ms.scale(1/2f, 1/2f, 1);
						ms.translate(-w, 0, 0);
						drawRightAligned(tr, ms, path, w-6-(tr.getWidth(name)*2), y+10, fontColor, false);
						if (i == 0) {
							drawRightAligned(tr, ms, String.format("§ltotal %.1f%%", pt.totalUsagePercentage), w-2, 20, fontColor, false);
						} else {
							drawRightAligned(tr, ms, String.format("%.1f%% §ltotal %.1f%%", pt.parentSectionUsagePercentage, pt.totalUsagePercentage), w-2, 20, fontColor, false);
						}
					ms.pop();
					y += 18;
					if (i-1 == cursorIndex) {
						String full = sel+"\u001E"+name;
						boolean first = true;
						List<ProfilerTiming> children = res.getTimings(full);
						if (children.size() > 2) {
							for (ProfilerTiming child : children) {
								if (first) {
									first = false;
									continue;
								}
								fontColor = 0;
								if ("unspecified".equals(child.name)) {
									drawBox(ms, w-140, y, 140, 12, 0xFF225522);
									fontColor = -1;
								} else {
									drawHashBox(ms, w-140, y, 140, 12, full+"\u001E"+child.name);
									drawBox(ms, w-140, y, 140, 12, 0x44FFFFFF);
								}
								drawRightAligned(tr, ms, child.name, w-2, y+1, fontColor, true);
								int cbw = (int)(child.parentSectionUsagePercentage*1.4);
								int cbw2 = (int)(child.totalUsagePercentage*1.4);
								drawBox(ms, w-cbw, y+11, cbw, 1, -1);
								drawBox(ms, w-cbw2, y+11, cbw2, 1, 0xFF000000);
								ms.push();
									ms.translate(w, y, 0);
									ms.scale(1/2f, 1/2f, 1);
									ms.translate(-w, 0, 0);
									drawRightAligned(tr, ms, String.format("%.1f%% §l %.1f%%", child.parentSectionUsagePercentage, child.totalUsagePercentage), w-16-(tr.getWidth(child.name)*2), 7, fontColor, false);
								ms.pop();
								y += 12;
							}
						}
					}
					i++;
				}
				stepOut = false;
				if (cursorIndex >= i-1) cursorIndex = i-2;
				if (cursorIndex < 0) cursorIndex = 0;
				if (overflowTop) {
					drawBox(ms, w-150, 0, 150, 12, -1);
					String str = "^ ^ ^          ^ ^ ^";
					tr.draw(ms, str, (w-75)-(tr.getWidth(str)/2), 5, 0);
					str = "More";
					tr.draw(ms, str, (w-75)-(tr.getWidth(str)/2), 3, 0);
				}
				if (y > h) {
					drawBox(ms, w-150, h-12, 150, 12, -1);
					String str = "^ ^ ^          ^ ^ ^";
					ms.push();
						ms.translate((w-75), h, 0);
						ms.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
						tr.draw(ms, str, -(tr.getWidth(str)/2), 4, 0);
					ms.pop();
					str = "More";
					tr.draw(ms, str, (w-75)-(tr.getWidth(str)/2), h-9, 0);
				} else {
					drawBox(ms, w-150, y, 150, 12, -1);
					ms.push();
						ms.translate(w, y, 0);
						ms.scale(1/2f, 1/2f, 1);
						ms.translate(-w, 0, 0);
						drawRightAligned(tr, ms, "§lYttr Profiler - Use numpad to control", w-2, 2, 0, false);
						drawRightAligned(tr, ms, "7: reset, 6/4: in/out, 8/2: up/down", w-2, 12, 0, false);
					ms.pop();
				}
			}
			ms.pop();
		}
	}
	
	private static void drawHashBox(MatrixStack ms, int x, int y, int w, int h, Object hash) {
		float hue = ((hash.hashCode()%1000)/100f)%1;
		if (hue < 0) hue += 1;
		drawBox(ms, x, y, w, h, MathHelper.hsvToRgb(hue, 0.6f, 0.9f)|0xFF000000);
	}
	
	private static void drawBox(MatrixStack ms, int x, int y, int w, int h, int color) {
		DrawableHelper.fill(ms, x, y, x+w, y+h, color);
	}

	public static void handleKey(InputUtil.Key key) {
		if (enabled) {
			if (key.getCode() == GLFW.GLFW_KEY_KP_2) {
				cursorIndex++;
			} else if (key.getCode() == GLFW.GLFW_KEY_KP_8) {
				cursorIndex--;
			} else if (key.getCode() == GLFW.GLFW_KEY_KP_6) {
				stepIn = true;
			} else if (key.getCode() == GLFW.GLFW_KEY_KP_4) {
				stepOut = true;
			} else if (key.getCode() == GLFW.GLFW_KEY_KP_7) {
				AccessorMinecraftClient amc = ((AccessorMinecraftClient)MinecraftClient.getInstance());
				amc.yttr$getTickTimeTracker().disable();
				amc.yttr$setTrackingTick(0);
			}
		}
	}
	
	private static void drawRightAligned(TextRenderer tr, MatrixStack ms, String s, int x, int y, int color, boolean colons) {
		int idx = s.indexOf(':');
		if (idx != -1) {
			String lhs = s.substring(0, idx);
			String rhs = s.substring(idx);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			x = tr.draw(ms, lhs, x-tr.getWidth(s), y, (color&0xFFFFFF)|0x55000000);
			tr.draw(ms, rhs, x, y, color);
			RenderSystem.disableBlend();
		} else {
			tr.draw(ms, s, x-tr.getWidth(s), y, color);
		}
	}
	
}
