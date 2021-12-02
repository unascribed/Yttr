package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.mixin.accessor.client.AccessorWorldRenderer;
import com.unascribed.yttr.network.MessageC2SRifleMode;
import com.unascribed.yttr.util.math.Interp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class RifleHUDRenderer extends IHasAClient {

	public static final int ANIM_TIME = 5;
	public static final float ANIM_TIMEf = ANIM_TIME;
	
	private static final Identifier MODES = new Identifier("yttr", "textures/gui/rifle_modes.png");
	private static final Identifier SCOPE = new Identifier("yttr", "textures/gui/riflescope.png");
	private static final Identifier SCOPEAMMO = new Identifier("yttr", "textures/gui/riflescopeammo.png");
	private static final Identifier TINYNUMBERS = new Identifier("yttr", "textures/gui/tiny_numbers.png");
	private static final Identifier CANICON = new Identifier("yttr", "textures/gui/riflecanicon.png");
	
	private static int ticksSinceOpen = -1;
	private static int ticksSinceClose = -1;
	private static int ticksSinceChange = 1000;
	private static int changeSignum = 1;
	
	public static int scopeTime;
	public static float scopeA;
	
	private static ItemStack rifleStack;
	private static RifleItem rifleItem;
	
	public static void render(MatrixStack matrices, float delta) {
		if (mc.player == null || rifleStack == null || rifleItem == null) return;
		if (scopeTime > 0) {
			boolean scoped = rifleStack.hasTag() && rifleStack.getTag().getBoolean("Scoped");
			scopeA = Interp.sCurve5(MathHelper.clamp((scopeTime+(scoped ? delta : -delta))/ANIM_TIMEf, 0, 1));
			mc.getTextureManager().bindTexture(SCOPE);
			matrices.push();
				matrices.translate(mc.getWindow().getScaledWidth()/2, mc.getWindow().getScaledHeight()/2, 0);
				int minDim = Math.min(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
				matrices.scale(minDim/2, minDim/2, 1);
				matrices.scale(1-((1-scopeA)/6), 1-((1-scopeA)/6), 1);
				RenderSystem.disableAlphaTest();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RifleMode mode = rifleItem.getMode(rifleStack);
				RenderSystem.color4f(((mode.color>>16)&0xFF)/255f, ((mode.color>>8)&0xFF)/255f, ((mode.color>>0)&0xFF)/255f, scopeA);
				int col = ((int)(scopeA*255)&0xFF)<<24;
				DrawableHelper.drawTexture(matrices, -1, -1, 0, 0, 2, 2, 2, 2);
				DrawableHelper.fill(matrices, -100, -2, -1, 2, col);
				DrawableHelper.fill(matrices, 1, -2, 100, 2, col);
				DrawableHelper.fill(matrices, -1, -2, 1, -1, col);
				DrawableHelper.fill(matrices, -1, 1, 1, 2, col);
				matrices.scale(0.25f, 0.75f, 1);
				RenderSystem.color4f(((mode.color>>16)&0xFF)/255f, ((mode.color>>8)&0xFF)/255f, ((mode.color>>0)&0xFF)/255f, scopeA);
				for (int p = 0; p < 2; p++) {
					RenderSystem.color4f(((mode.color>>16)&0xFF)/255f, ((mode.color>>8)&0xFF)/255f, ((mode.color>>0)&0xFF)/255f, scopeA/(p+1));
					mc.getTextureManager().bindTexture(MODES);
					RenderSystem.disableAlphaTest();
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					{
						float u = mode.ordinal()*2;
						float v = 0;
						float width = 2;
						float height = 2;
						int textureWidth = RifleMode.ALL_VALUES.size()*2;
						int textureHeight = 2;
						Matrix4f mat = matrices.peek().getModel();
						float x1 = 4.4f-(p/8f);
						float x2 = x1 + width;
						float y1 = -0.88f;
						float y2 = y1 + height;
						float minU = (u + 0) / textureWidth;
						float maxU = (u + width) / textureWidth;
						float minV = (v + 0) / textureHeight;
						float maxV = (v + height) / textureHeight;
						BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
						bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
						bufferBuilder.vertex(mat, x1, y2-0.2f, 0).texture(minU, maxV).next();
						bufferBuilder.vertex(mat, x2, y2, 0).texture(maxU, maxV).next();
						bufferBuilder.vertex(mat, x2, y1, 0).texture(maxU, minV).next();
						bufferBuilder.vertex(mat, x1, y1-0.2f, 0).texture(minU, minV).next();
						bufferBuilder.end();
						BufferRenderer.draw(bufferBuilder);
					}
					mc.getTextureManager().bindTexture(SCOPEAMMO);
					matrices.push(); {
						float mH = 1.8f;
						float f = (1-(rifleItem.getRemainingAmmo(rifleStack)/(float)rifleItem.getMaxAmmo(rifleStack)))*mH;
						matrices.scale(4, 4/3f, 0);
						float u = 0;
						float v = f;
						float width = mH;
						float height = mH-f;
						float textureWidth = mH;
						float textureHeight = mH;
						Matrix4f mat = matrices.peek().getModel();
						float x1 = -1.3f+(p/32f);
						float x2 = x1 + width;
						float y1 = f-(mH/2);
						float y2 = y1 + height;
						float minU = (u + 0) / textureWidth;
						float maxU = (u + width) / textureWidth;
						float minV = (v + 0) / textureHeight;
						float maxV = (v + height) / textureHeight;
						BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
						bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
						bufferBuilder.vertex(mat, x1, y2, 0).texture(minU, maxV).next();
						bufferBuilder.vertex(mat, x2, y2, 0).texture(maxU, maxV).next();
						bufferBuilder.vertex(mat, x2, y1, 0).texture(maxU, minV).next();
						bufferBuilder.vertex(mat, x1, y1, 0).texture(minU, minV).next();
						bufferBuilder.end();
						BufferRenderer.draw(bufferBuilder);
					}
					matrices.pop();
				}
			matrices.pop();
		} else if (ticksSinceOpen > 0 || (ticksSinceClose != -1 && ticksSinceClose < ANIM_TIME)) {
			float mainA = Interp.sCurve5((Math.min((ticksSinceOpen > 0 ? ticksSinceOpen+delta : ANIM_TIME-(ticksSinceClose+delta)), ANIM_TIME)/ANIM_TIMEf));
			RifleMode current = rifleItem.getMode(rifleStack);
			matrices.push();
				matrices.translate(mc.getWindow().getScaledWidth()/2, mc.getWindow().getScaledHeight()/2, 0);
				float t = Interp.sCurve5(1-(Math.min(ticksSinceChange+delta, ANIM_TIME)/ANIM_TIMEf))*changeSignum;
				for (int i = -3-(Math.abs(changeSignum)); i <= 3+(Math.abs(changeSignum)); i++) {
					int j = (current.effectiveOrdinal()+i)%RifleMode.VALUES.size();
					if (j < 0) j = RifleMode.VALUES.size()+j;
					RifleMode mode = RifleMode.VALUES.get(j);
					float f = (float)((1-((i+t)/8f))*Math.PI);
					float a = (1-(Math.abs(i+t)/4f))*mainA;
					if (a < 0) a = 0;
					mc.getTextureManager().bindTexture(MODES);
					int ammo = rifleItem.getPotentialAmmoCount(mc.player, mode);
					boolean canned = rifleItem.isAmmoCanned(mc.player, mode);
					if (ammo == 0) {
						RenderSystem.color4f(0.25f, 0.25f, 0.25f, a);
					} else {
						RenderSystem.color4f(((mode.color>>16)&0xFF)/255f, ((mode.color>>8)&0xFF)/255f, ((mode.color>>0)&0xFF)/255f, a);
					}
					int x = (int)((MathHelper.sin(f)*64)-8);
					int y = (int)((MathHelper.cos(f)*48)+16);
					// text renderer messes up the render state, so we have to set it every loop
					RenderSystem.disableAlphaTest();
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					DrawableHelper.drawTexture(matrices, x, y, mode.ordinal()*16, 0, 16, 16, RifleMode.ALL_VALUES.size()*16, 16);
					if (a > 0.1f) {
						if (ammo == -1) {
							int textCol = 0x00FFFFFF;
							textCol |= ((int)(a*255)&0xFF)<<24;
							mc.textRenderer.drawWithShadow(matrices, "∞", x+16-(mc.textRenderer.getWidth("∞")), y+8, textCol);
						} else {
							String str = Integer.toString(ammo);
							int w = str.length()*4;
							for (int p = 0; p < 2; p++) {
								int cx = x+18-w;
								int cy = y+12;
								if (p == 0) {
									RenderSystem.color4f(0.25f, 0.25f, 0.25f, a);
								} else {
									RenderSystem.color4f(1, 1, 1, a);
									cx--;
									cy--;
								}
								if (canned) {
									MinecraftClient.getInstance().getTextureManager().bindTexture(CANICON);
									DrawableHelper.drawTexture(matrices, x+18-5, y+12-7, 300, 0, 0, 5, 5, 5, 5);
								}
								MinecraftClient.getInstance().getTextureManager().bindTexture(TINYNUMBERS);
								for (int ci = 0; ci < str.length(); ci++) {
									int cj = str.charAt(ci)-'0';
									int u = (cj%5)*3;
									int v = (cj/5)*5;
									DrawableHelper.drawTexture(matrices, cx, cy, 300, u, v, 3, 5, 10, 15);
									cx += 4;
								}
							}
						}
					}
				}
			matrices.pop();
			RenderSystem.enableAlphaTest();
		}
	}
	
	public static void tick() {
		if (mc.player == null) return;
		ItemStack stack = mc.player.getMainHandStack();
		if (!(stack.getItem() instanceof RifleItem)) {
			if ((ticksSinceOpen == -1 && ticksSinceClose == -1) || ticksSinceClose > ANIM_TIME) {
				ticksSinceChange = 1000;
				ticksSinceOpen = -1;
				ticksSinceClose = -1;
				rifleStack = null;
				rifleItem = null;
			} else {
				if (ticksSinceOpen > 0) ticksSinceOpen = -1;
				ticksSinceClose++;
			}
			if (scopeTime > 0) {
				scopeTime = 0;
				((AccessorWorldRenderer)mc.worldRenderer).yttr$setNeedsTerrainUpdate(true);
			}
			return;
		}
		if (ticksSinceChange > ANIM_TIME) {
			changeSignum = 0;
		}
		rifleStack = stack;
		rifleItem = (RifleItem)stack.getItem();
		if (mc.options.keySwapHands.isPressed() || mc.options.keySwapHands.wasPressed()) {
			if (ticksSinceOpen == -1) {
				ticksSinceOpen = 0;
				ticksSinceClose = -1;
			} else {
				ticksSinceOpen++;
				ticksSinceClose = -1;
			}
			RifleMode current = rifleItem.getMode(stack);
			RifleMode next = current;
			// drain timesPressed to prevent vanilla behavior
			while (mc.options.keySwapHands.wasPressed()) {}
			if (mc.options.keyUse.wasPressed()) {
				while (mc.options.keyUse.wasPressed()) {}
				mc.options.keyUse.setPressed(false);
				if (changeSignum == 0) {
					ticksSinceChange = 0;
				} else {
					ticksSinceChange = Math.max(0, ticksSinceChange-3);
				}
				changeSignum++;
				next = current.next();
			}
			if (mc.options.keyAttack.wasPressed()) {
				while (mc.options.keyAttack.wasPressed()) {}
				mc.options.keyAttack.setPressed(false);
				if (changeSignum == 0) {
					ticksSinceChange = 0;
				} else {
					ticksSinceChange = Math.max(0, ticksSinceChange-3);
				}
				changeSignum--;
				next = current.prev();
			}
			if (next != current) {
				rifleItem.setMode(stack, next);
				rifleItem.setRemainingAmmo(stack, 0);
				new MessageC2SRifleMode(next).sendToServer();
			}
		} else if (ticksSinceOpen > 0 || ticksSinceClose >= 0) {
			ticksSinceOpen = -1;
			if (ticksSinceClose == -1) {
				ticksSinceClose = 0;
			} else {
				ticksSinceClose++;
			}
		}
		boolean scoped = rifleStack.hasTag() && rifleStack.getTag().getBoolean("Scoped");
		if (scoped) {
			if (scopeTime < ANIM_TIME) scopeTime++;
		} else {
			if (scopeTime > 0) {
				scopeTime--;
				((AccessorWorldRenderer)mc.worldRenderer).yttr$setNeedsTerrainUpdate(true);
			}
		}
		ticksSinceChange++;
	}

}
