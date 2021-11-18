package com.unascribed.yttr.client.render;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.ReplicatorShapes;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.math.Interp;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class ReplicatorRenderer extends IHasAClient {

	public static final Set<ReplicatorBlockEntity> replicators = Sets.newLinkedHashSet();
	public static final Set<ReplicatorBlockEntity> removing = Sets.newLinkedHashSet();
	private static final List<ReplicatorBlockEntity> renderList = Lists.newArrayList();
	
	private static final Screen dummyScreen = new Screen(new LiteralText("")) {};
	private static final Random rand = new Random();
	
	public static boolean renderOutline(WorldRenderContext wrc, BlockOutlineContext boc) {
		if (boc.blockState().isOf(YBlocks.REPLICATOR)) {
			BlockEntity be = wrc.world().getBlockEntity(boc.blockPos());
			if (be instanceof ReplicatorBlockEntity) {
				ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
				MatrixStack matrices = wrc.matrixStack();
				matrices.push();
				matrices.translate(boc.blockPos().getX()-boc.cameraX(), boc.blockPos().getY()-boc.cameraY(), boc.blockPos().getZ()-boc.cameraZ());
				matrices.translate(0.5, 0.5, 0.5);
				rand.setSeed(((ReplicatorBlockEntity) be).seed);
				
				if (rbe.clientAge+wrc.tickDelta() < 5) {
					float a = Interp.sCurve5((rbe.clientAge+wrc.tickDelta())/5f);
					matrices.scale(a, a, a);
				}
				
				float t = rand.nextInt(200)+rbe.clientAge+wrc.tickDelta();
				
				int shape = rand.nextInt(ReplicatorShapes.ALL.size());
				rand.nextInt(ReplicatorShapes.ALL.size());
				
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				matrices.scale(1.4f-(rand.nextFloat()*0.65f), 1.4f-(rand.nextFloat()*0.65f), 1.4f-(rand.nextFloat()*0.65f));
		
				VertexConsumer vc = wrc.consumers().getBuffer(RenderLayer.getLines());
				switch (shape) {
					case 0:
						matrices.scale(0.5f, 0.5f, 0.5f);
						ReplicatorShapes.octahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
					case 1:
						matrices.scale(0.509165f, 0.509165f, 0.509165f);
						ReplicatorShapes.dodecahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
					case 2:
						matrices.scale(0.5f, 0.5f, 0.5f);
						ReplicatorShapes.icosahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
				}
				matrices.pop();
				return false;
			}
		}
		return true;
	}
	
	public static void render(MatrixStack matrices, float tickDelta, int seed, ItemStack item, BlockPos pos, int ticks, Camera cam, int pass) {
		if (pass < 2 || pass == -1) {
			matrices.push();
			matrices.translate(0.5, 0.5, 0.5);
			if (ticks+tickDelta < 5) {
				float a = Interp.sCurve5((ticks+tickDelta)/5f);
				matrices.scale(a, a, a);
			}
			
			rand.setSeed(seed);
			
			float t = rand.nextInt(200)+ticks+tickDelta;
			
			if (pass == 0 || pass == -1) {
				matrices.push();
				ItemRenderer ir = mc.getItemRenderer();
				BakedModel model = ir.getModels().getModel(item);
				if (pass == -1) {
					// bigger items in GUI so you can tell what they are
					// the clipping evident when an item exceeds the bounds of the inner shape is less obvious in a gui
					// also make them face the right direction
					BakedModel ours = ir.getModels().getModel(YItems.REPLICATOR);
					Transformation undo = ours.getTransformation().gui;
					matrices.scale(1/undo.scale.getX(), 1/undo.scale.getY(), 1/undo.scale.getZ());
					Quaternion q = new Quaternion(undo.rotation.getX(), undo.rotation.getY(), undo.rotation.getZ(), true);
					q.conjugate();
					matrices.multiply(q);
					matrices.translate(-undo.translation.getX(), -undo.translation.getY(), -undo.translation.getZ());
					model.getTransformation().gui.apply(false, matrices);
					matrices.scale(0.65f, 0.65f, 0.65f);
				} else {
					matrices.scale(0.35f, 0.35f, 0.35f);
				}
				if (cam != null) {
					matrices.multiply(cam.getRotation());
				}
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin((t/6)%((float)Math.PI*2))*10));
			
				VertexConsumerProvider.Immediate imm = mc.getBufferBuilders().getEntityVertexConsumers();
				ir.renderItem(item, Mode.NONE, false, matrices, imm, LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, model);
				imm.draw();
				matrices.pop();
			}
			
			if (pass == 1) {
				VertexBuffer solid1 = ReplicatorShapes.ALL.get(rand.nextInt(ReplicatorShapes.ALL.size()));
				VertexBuffer solid2 = ReplicatorShapes.ALL.get(rand.nextInt(ReplicatorShapes.ALL.size()));
				
				RenderSystem.disableTexture();
				RenderSystem.enableRescaleNormal();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableAlphaTest();
				
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(t*(rand.nextFloat()*2)));
				float xSo = (rand.nextFloat()*0.65f);
				float ySo = (rand.nextFloat()*0.65f);
				float zSo = (rand.nextFloat()*0.65f);
				if (cam == null) {
					xSo = ySo = zSo = 0;
				}
				matrices.scale(1.4f-xSo, 1.4f-ySo, 1.4f-zSo);
		
				RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
				float r = rand.nextFloat()/2;
				float g = rand.nextFloat()/2;
				float b = (0.25f+(rand.nextFloat()*0.75f))/2;
				RenderSystem.disableLighting();
				solid1.bind();
				matrices.push();
				for (int i = 0; i < (MinecraftClient.isFancyGraphicsOrBetter() ? 6 : 1); i++) {
					RenderSystem.color4f(r, g, b, i == 0 ? 0.4f : 0.1f);
					ReplicatorShapes.POSITION_NORMAL.startDrawing(0);
					YttrClient.drawBufferWithoutClobberingGLMatrix(solid1, matrices.peek().getModel(), GL11.GL_TRIANGLES);
					ReplicatorShapes.POSITION_NORMAL.endDrawing();
					matrices.scale(1.05f, 1.05f, 1.05f);
				}
				matrices.pop();
				VertexBuffer.unbind();
				
				matrices.scale(0.9f-(rand.nextFloat()*0.25f), 0.9f-(rand.nextFloat()*0.25f), 0.9f-(rand.nextFloat()*0.25f));

				RenderSystem.defaultBlendFunc();
				DiffuseLighting.enable();
				RenderSystem.color4f(rand.nextFloat(), 0.25f+(rand.nextFloat()*0.75f), rand.nextFloat(), 0.25f);
				solid2.bind();
				ReplicatorShapes.POSITION_NORMAL.startDrawing(0);
				YttrClient.drawBufferWithoutClobberingGLMatrix(solid2, matrices.peek().getModel(), GL11.GL_TRIANGLES);
				ReplicatorShapes.POSITION_NORMAL.endDrawing();
				VertexBuffer.unbind();
				
				RenderSystem.enableTexture();
				RenderSystem.disableRescaleNormal();
				RenderSystem.disableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableAlphaTest();
				
				RenderSystem.color3f(1, 1, 1);
			}
			matrices.pop();
		}
		
		if (pass == 2 && !item.isEmpty() && mc.crosshairTarget.getType() == Type.BLOCK) {
			BlockHitResult bhr = (BlockHitResult)mc.crosshairTarget;
			if (bhr.getBlockPos().equals(pos)) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableLighting();
				matrices.push();
				matrices.translate(0.5, 0.5, 0.5);
				if (cam != null) matrices.multiply(cam.getRotation());
				matrices.scale(-0.0125F, -0.0125F, 0.01f);
				matrices.translate(0, -50, -400);

				dummyScreen.init(mc, 4000, 4000);
				List<OrderedText> tip = Lists.transform(dummyScreen.getTooltipFromItem(item), Text::asOrderedText);
				int width = 0;
				int height = tip.size()*8;
				for (OrderedText ot : tip) {
					width = Math.max(width, mc.textRenderer.getWidth(ot));
				}
				matrices.translate(-(width+16)/2f, -height, 0);
				dummyScreen.renderOrderedTooltip(matrices, tip, 0, 0);
				
				matrices.pop();
				RenderSystem.disableBlend();
			}
		}
	}
	
	public static void render(WorldRenderContext wrc) {
		wrc.profiler().swap("yttr:replicators");
		if (!replicators.isEmpty() || !removing.isEmpty()) {
			renderList.clear();
			renderList.addAll(replicators);
			renderList.addAll(removing);
			wrc.profiler().push("prepare");
			for (ReplicatorBlockEntity rbe : renderList) {
				if (rbe.clientAge < 1) continue;
				double dist = rbe.getPos().getSquaredDistance(wrc.camera().getPos(), false);
				if (dist < 64*64 && wrc.frustum().isVisible(new Box(rbe.getPos()))) {
					rbe.distTmp = dist;
				}
			}
			wrc.profiler().swap("sort");
			Collections.sort(renderList, (a, b) -> Double.compare(b.distTmp, a.distTmp));
			wrc.profiler().swap("render");
			MatrixStack matrices = wrc.matrixStack();
			RenderSystem.pushMatrix();
			RenderSystem.loadIdentity();
			matrices.push();
			Vec3d cam = wrc.camera().getPos();
			matrices.translate(-cam.x, -cam.y, -cam.z);
			for (int pass = 0; pass < 3; pass++) {
				wrc.profiler().push("pass"+pass);
				RenderSystem.depthMask(pass == 0);
				RenderSystem.enableDepthTest();
				RenderSystem.depthFunc(GL11.GL_LESS);
				for (ReplicatorBlockEntity rbe : renderList) {
					matrices.push();
					matrices.translate(rbe.getPos().getX(), rbe.getPos().getY(), rbe.getPos().getZ());
					if (rbe.isRemoved()) {
						float a = Interp.sCurve5(Math.max(0, 1-((rbe.removedTicks+wrc.tickDelta())/10f)));
						matrices.translate(0.5, 0.5, 0.5);
						matrices.scale(a, a, a);
						matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(a*720));
						matrices.translate(-0.5, -0.5, -0.5);
					}
					render(matrices, wrc.tickDelta(), rbe.seed, rbe.item, rbe.getPos(), rbe.clientAge, wrc.camera(), pass);
					matrices.pop();
				}
				RenderSystem.depthMask(true);
				wrc.profiler().pop();
			}
			matrices.pop();
			RenderSystem.popMatrix();
			wrc.profiler().pop();
		}
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.disableLighting();
		wrc.profiler().swap("particles");
	}
	
	public static void tick() {
		if (mc.world != null) {
			for (BlockEntity be : mc.world.blockEntities) {
				if (be instanceof ReplicatorBlockEntity) {
					ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
					rbe.clientTick();
				}
			}
		} else {
			replicators.clear();
			removing.clear();
		}
		Iterator<ReplicatorBlockEntity> iter = removing.iterator();
		while (iter.hasNext()) {
			ReplicatorBlockEntity rbe = iter.next();
			rbe.clientTick();
			if (!rbe.isRemoved() || rbe.removedTicks > 10) {
				rbe.removedTicks = 0;
				iter.remove();
			}
		}
	}

}
