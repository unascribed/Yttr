package com.unascribed.yttr.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.item.CleaverItem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CleaverUI {

	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static boolean render(WorldRenderContext wrc, BlockOutlineContext boc) {
		ItemStack held = mc.player.getStackInHand(Hand.MAIN_HAND);
		if (held.getItem() instanceof CleaverItem) {
			CleaverItem ci = (CleaverItem)held.getItem();
			HitResult tgt = mc.crosshairTarget;
			if (tgt instanceof BlockHitResult && (!ci.requiresSneaking() || boc.entity().isSneaking())) {
				BlockPos cleaving = ci.getCleaveBlock(held);
				if (cleaving == null && tgt.getPos().squaredDistanceTo(boc.cameraX(), boc.cameraY(), boc.cameraZ()) > 2*2) return true;
				BlockPos pos = cleaving == null ? boc.blockPos() : cleaving;
				BlockState bs = wrc.world().getBlockState(pos);
				if (bs.isSolidBlock(wrc.world(), pos)) {
					GlStateManager.pushMatrix();
					GlStateManager.multMatrix(wrc.matrixStack().peek().getModel());
					GlStateManager.translated(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
					GlStateManager.disableTexture();
					RenderSystem.defaultBlendFunc();
					GlStateManager.enableBlend();
					GL11.glEnable(GL11.GL_POINT_SMOOTH);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					float scale = (float)mc.getWindow().getScaleFactor();
					int sd = CleaverItem.SUBDIVISIONS;
					Vec3d cleaveStart = ci.getCleaveStart(held);
					boolean anySelected = false;
					float selectedX = 0;
					float selectedY = 0;
					float selectedZ = 0;
					for (int x = 0; x <= sd; x++) {
						for (int y = 0; y <= sd; y++) {
							for (int z = 0; z <= sd; z++) {
								if ((x > 0 && x < sd) &&
										(y > 0 && y < sd) &&
										(z > 0 && z < sd)) {
									continue;
								}
								
								float wX = x/(float)sd;
								float wY = y/(float)sd;
								float wZ = z/(float)sd;
								boolean isStart = cleaveStart != null && cleaveStart.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05;
								boolean selected = false;
								float a;
								if (!isStart) {
									double dist = tgt.getPos().squaredDistanceTo(pos.getX()+wX, pos.getY()+wY, pos.getZ()+wZ);
									final double maxDist = 0.75;
									if (dist > maxDist*maxDist) continue;
									selected = dist < 0.1*0.1;
									double distSq = Math.sqrt(dist);
									a = (float)((maxDist-distSq)/maxDist);
								} else {
									a = 1;
								}
								float r = 1;
								float g = 1;
								float b = 1;
								float size = a*10;
								if (isStart) {
									size = 8;
									g = 0;
									b = 0;
								} else if (selected) {
									size = 15;
									b = 0;
									anySelected = true;
									selectedX = wX;
									selectedY = wY;
									selectedZ = wZ;
								}
								GL11.glPointSize(size*scale);
								GlStateManager.color4f(r, g, b, a);
								GL11.glBegin(GL11.GL_POINTS);
								GL11.glVertex3f(wX, wY, wZ);
								GL11.glEnd();
							}
						}
					}
					if (anySelected && cleaveStart != null) {
						GlStateManager.color4f(1, 0.5f, 0, 0.5f);
						GL11.glLineWidth(4*scale);
						GL11.glBegin(GL11.GL_LINES);
						GL11.glVertex3d(cleaveStart.x, cleaveStart.y, cleaveStart.z);
						GL11.glVertex3f(selectedX, selectedY, selectedZ);
						GL11.glEnd();
					}
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
					GlStateManager.enableTexture();
				}
			}
		}
		return true;
	}

}
