package com.unascribed.yttr.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.item.CleaverItem;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CleaverUI {

	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static boolean render(WorldRenderContext wrc, BlockOutlineContext boc) {
		ItemStack held = mc.player.getStackInHand(Hand.MAIN_HAND);
		if (boc.blockState().getBlock() == YBlocks.CLEAVED_BLOCK) {
			BlockEntity be = wrc.world().getBlockEntity(boc.blockPos());
			if (be instanceof CleavedBlockEntity) {
				wrc.matrixStack().push();
				BlockPos pos = boc.blockPos();
				wrc.matrixStack().translate(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
				List<Polygon> polys = ((CleavedBlockEntity)be).getPolygons();
				// skip the "joiner" polygon to avoid an ugly line down the middle of the joined face
				// TODO why does the line happen? is the joiner polygon invalid?
				for (Polygon pg : polys.subList(0, polys.size()-1)) {
					pg.forEachDEdge((de) -> {
						boc.vertexConsumer().vertex(wrc.matrixStack().peek().getModel(), (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z).color(0, 0, 0, 0.4f).next();
						boc.vertexConsumer().vertex(wrc.matrixStack().peek().getModel(), (float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z).color(0, 0, 0, 0.4f).next();
					});
				}
				wrc.matrixStack().pop();
				return false;
			}
		} else if (held.getItem() instanceof CleaverItem) {
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
					Vec3d cleaveCorner = ci.getCleaveCorner(held);
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
								boolean highlight = (cleaveStart != null && cleaveStart.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05) || (cleaveCorner != null && cleaveCorner.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05);
								boolean selected = false;
								float a;
								if (!highlight) {
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
								if (highlight) {
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
					if (anySelected && cleaveStart != null && cleaveCorner != null) {
						final float TAU = (float)(Math.PI*2);
						float t = (wrc.world().getTime()+wrc.tickDelta())/5;
						float a = (MathHelper.sin(t%TAU)+1)/2;
						GlStateManager.color4f(1, 0.25f, 0, 0.1f+(a*0.3f));
						GlStateManager.disableCull();
						GlStateManager.enableDepthTest();
						GlStateManager.enablePolygonOffset();
						GlStateManager.polygonOffset(-3, -3);
						List<Polygon> cleave = CleaverItem.performCleave(cleaveStart, cleaveCorner, new Vec3d(selectedX, selectedY, selectedZ), CleavedBlockEntity.cube(), true);
						for (Polygon polygon : cleave) {
							GL11.glBegin(GL11.GL_POLYGON);
							polygon.forEachDEdge((de) -> {
								GL11.glVertex3d(de.srcPoint().x, de.srcPoint().y, de.srcPoint().z);
							});
							GL11.glEnd();
						}
						GlStateManager.disablePolygonOffset();
						GlStateManager.color4f(1, 0.25f, 0, 0.05f+(a*0.1f));
						GlStateManager.disableDepthTest();
						for (Polygon polygon : cleave) {
							GL11.glBegin(GL11.GL_POLYGON);
							polygon.forEachDEdge((de) -> {
								GL11.glVertex3d(de.srcPoint().x, de.srcPoint().y, de.srcPoint().z);
							});
							GL11.glEnd();
						}
						GlStateManager.enableCull();
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
