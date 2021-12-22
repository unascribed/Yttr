package com.unascribed.yttr.client.render;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.mechanics.HaloBlockEntity;
import com.unascribed.yttr.mixin.accessor.client.AccessorFrustum;
import com.unascribed.yttr.util.MysticSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class LampRenderer extends IHasAClient {

	private static final Map<BlockEntity, Object> lastState = new Object2ObjectOpenHashMap<>();
	private static final Multimap<ChunkSectionPos, BlockEntity> lamps = Multimaps.newSetMultimap(new Object2ObjectOpenHashMap<>(), ReferenceOpenHashSet::new);
	private static final Map<ChunkSectionPos, VertexBuffer> buffers = new Object2ObjectOpenHashMap<>();

	public static void clearCache() {
		buffers.values().forEach(VertexBuffer::close);
		buffers.clear();
	}

	public static void render(World world, MatrixStack matrices, VertexConsumer vc, BlockState state, int color, @Nullable Direction facing, @Nullable BlockPos pos) {
		if (color == 0) color = 0x222222;
		float r = ((color >> 16)&0xFF)/255f;
		float g = ((color >> 8)&0xFF)/255f;
		float b = (color&0xFF)/255f;
		BakedModel base = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		BakedModel bm;
		try {
			YttrClient.retrievingHalo = true;
			bm = base.getOverrides().apply(base, ItemStack.EMPTY, MinecraftClient.getInstance().world, MinecraftClient.getInstance().player);
		} finally {
			YttrClient.retrievingHalo = false;
		}
		if (bm == null) return;
		DelegatingVertexConsumer dvc = new DelegatingVertexConsumer(vc) {
			@Override
			public VertexConsumer normal(float x, float y, float z) {
				return this;
			}
			@Override
			public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
				return this;
			}
			@Override
			public VertexConsumer light(int u, int v) {
				return this;
			}
			@Override
			public VertexConsumer light(int uv) {
				return this;
			}
			@Override
			public VertexConsumer overlay(int u, int v) {
				return this;
			}
			@Override
			public VertexConsumer overlay(int uv) {
				return this;
			}
		};

		matrices.push();

		if (facing != null) {
			int x = 0;
			int y = 0;
			switch (facing) {
				case DOWN: break;
				case WEST: x = 90; y = 90; break;
				case NORTH: x = 90; break;
				case SOUTH: x = 90; y = 180; break;
				case EAST: x = 90; y = 270; break;
				case UP: x = 180; break;
			}
			matrices.translate(0.5, 0.5, 0.5);
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(y));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(x));
			matrices.translate(-0.5, -0.5, -0.5);
		}

		for (BakedQuad bq : bm.getQuads(state, null, world.random)) {
			dvc.quad(matrices.peek(), bq, r, g, b, 0, 0);
		}
		for (Direction dir : Direction.values()) {
			if (pos == null || Block.shouldDrawSide(state, MinecraftClient.getInstance().world, pos, dir)) {
				for (BakedQuad bq : bm.getQuads(state, dir, world.random)) {
					dvc.quad(matrices.peek(), bq, r, g, b, 0, 0);
				}
			}
		}

		matrices.pop();
	}

	public static void render(WorldRenderContext wrc) {
		wrc.profiler().swap("yttr:lamps");
		if (!lamps.isEmpty()) {
			wrc.profiler().push("prepare");
			MysticSet<ChunkSectionPos> needsRebuild = MysticSet.of();
			for (BlockEntity be : lamps.values()) {
				if (!(be instanceof HaloBlockEntity)) continue;
				Object s = ((HaloBlockEntity)be).getStateObject();
				ChunkSectionPos csp = ChunkSectionPos.from(be.getPos());
				if (lastState.get(be) != s || !buffers.containsKey(csp)) {
					lastState.put(be, s);
					needsRebuild = needsRebuild.add(csp);
				}
			}
			wrc.profiler().swap("rebuild");
			MatrixStack scratch = new MatrixStack();
			for (ChunkSectionPos csp : needsRebuild.mundane()) {
				Collection<BlockEntity> l = lamps.get(csp);
				if (l.isEmpty()) {
					if (buffers.containsKey(csp)) {
						buffers.remove(csp).close();
					}
					continue;
				}
				BufferBuilder vc = new BufferBuilder(24 * VertexFormats.POSITION_COLOR_TEXTURE.getVertexSize() * l.size());
				vc.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
				for (BlockEntity be : l) {
					if (!(be instanceof HaloBlockEntity) || !((HaloBlockEntity)be).shouldRenderHalo()) continue;
					scratch.push();
						scratch.translate(be.getPos().getX()-csp.getMinX(), be.getPos().getY()-csp.getMinY(), be.getPos().getZ()-csp.getMinZ());
						int color = ((HaloBlockEntity)be).getGlowColor();
						BlockState state = be.getCachedState();
						Direction facing = ((HaloBlockEntity)be).getFacing();
						render(mc.world, scratch, vc, state, color, facing, be.getPos());
					scratch.pop();
				}
				vc.end();
				VertexBuffer vb = buffers.computeIfAbsent(csp, blah -> new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE));
				vb.upload(vc);
				buffers.put(csp, vb);
			}
			wrc.profiler().swap("render");
			MatrixStack matrices = wrc.matrixStack();
			RenderSystem.pushMatrix();
			RenderSystem.loadIdentity();
			matrices.push();
			Vec3d cam = wrc.camera().getPos();
			matrices.translate(-cam.x, -cam.y, -cam.z);
			for (ChunkSectionPos pos : buffers.keySet()) {
				if (((AccessorFrustum)wrc.frustum()).yttr$isVisible(pos.getMinX()-1, pos.getMinY()-1, pos.getMinZ()-1, pos.getMaxX()+2, pos.getMaxY()+2, pos.getMaxZ()+2)) {
					matrices.push();
						matrices.translate(pos.getMinX(), pos.getMinY(), pos.getMinZ());
						VertexBuffer buf = buffers.get(pos);
						buf.bind();
						YRenderLayers.getLampHalo().startDrawing();
						VertexFormats.POSITION_COLOR_TEXTURE.startDrawing(0L);
						YttrClient.drawBufferWithoutClobberingGLMatrix(buf, matrices.peek().getModel(), GL11.GL_QUADS);
						VertexFormats.POSITION_COLOR_TEXTURE.endDrawing();
						YRenderLayers.getLampHalo().endDrawing();
						VertexBuffer.unbind();
					matrices.pop();
				}
			}
			matrices.pop();
			RenderSystem.popMatrix();
			wrc.profiler().pop();
		}
		wrc.profiler().swap("particles");
	}

	public static void tick() {
		if (mc.world != null) {
			Iterator<BlockEntity> iter = lamps.values().iterator();
			while (iter.hasNext()) {
				BlockEntity be = iter.next();
				if (be.isRemoved() || be.getWorld() != mc.world) {
					ChunkSectionPos cs = ChunkSectionPos.from(be.getPos());
					if (buffers.containsKey(cs)) {
						buffers.remove(cs).close();
					}
					iter.remove();
				}
			}
			for (BlockEntity be : mc.world.blockEntities) {
				if (be instanceof HaloBlockEntity) {
					ChunkSectionPos cs = ChunkSectionPos.from(be.getPos());
					if (!lamps.containsEntry(cs, be)) {
						lamps.put(cs, be);
					}
				}
			}
		} else {
			lamps.clear();
			lastState.clear();
			clearCache();
		}
	}

}
