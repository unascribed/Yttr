package com.unascribed.yttr.client.cache;

import java.util.Random;

import com.google.gson.internal.UnsafeAllocator;
import com.unascribed.yttr.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Plane;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import com.google.common.collect.Iterables;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockMeshes {

	private static final MatrixStack IDENTITY = new MatrixStack();
	
	private static final UVObserver uvo = new UVObserver();
	
	private static class DummySprite extends Sprite {

		private float minU, minV, maxU, maxV;
		
		protected DummySprite() {
			super(null, new Info(null, 0, 0, null), 0, 0, 0, 0, 0, null);
			// NOT CALLED
		}
		
		public static DummySprite create(float minU, float minV, float maxU, float maxV) {
			try {
				DummySprite ds = UnsafeAllocator.create().newInstance(DummySprite.class);
				ds.minU = minU;
				ds.minV = minV;
				ds.maxU = maxU;
				ds.maxV = maxV;
				return ds;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public float getMinU() {
			return minU;
		}
		
		@Override
		public float getMinV() {
			return minV;
		}
		
		@Override
		public float getMaxU() {
			return maxU;
		}
		
		@Override
		public float getMaxV() {
			return maxV;
		}
		
	}
	
	public static Mesh getMesh(CleavedBlockEntity entity) {
		if (entity.clientCacheData instanceof Mesh) return (Mesh)entity.clientCacheData;
		if (!RendererAccess.INSTANCE.hasRenderer()) return null;
		MinecraftClient.getInstance().getProfiler().push("yttr:cleaved_modelgen");
		BakedModel donor = MinecraftClient.getInstance().getBlockRenderManager().getModel(entity.getDonor());
		Renderer r = RendererAccess.INSTANCE.getRenderer();
		MeshBuilder bldr = r.meshBuilder();
		QuadEmitter qe = bldr.getEmitter();
		Random rand = new Random(7);
		BakedQuad firstNullQuad = Iterables.getFirst(donor.getQuads(entity.getDonor(), null, rand), null);
		Sprite particle = donor.getSprite();
		for (Polygon p : entity.getPolygons()) {
			Plane plane = p.plane();
			Direction face = findClosestFace(plane.normal());
			BakedQuad firstQuad = Iterables.getFirst(donor.getQuads(entity.getDonor(), face, rand), firstNullQuad);
			Sprite sprite;
			if (firstQuad == null) {
				sprite = particle;
			} else {
				uvo.reset();
				uvo.quad(IDENTITY.peek(), firstQuad, 0, 0, 0, 0, 0);
				sprite = DummySprite.create(uvo.getMinU(), uvo.getMinV(), uvo.getMaxU(), uvo.getMaxV());
			}
			if (p.nPoints() <= 2) {
				// ???
			} else if (p.nPoints() == 3) {
				// trivial case: triangle. make a degenerate quad
				buildTrivial(sprite, qe, p, false);
			} else if (p.nPoints() == 4) {
				// ideal case: it's already a quad
				buildTrivial(sprite, qe, p, false);
			} else {
				// worst case: need to triangulate
				// this isn't Optimal™, it's a trivial convex-only triangulation
				// but hey, it works, and doesn't make my head hurt
				Vec3d origin = p.first().srcPoint();
				int c = -1;//0x0000FF;
				for (DEdge de : p) {
					if (de == p.first()) continue;
					// each triangle is a degenerate quad
					// it'd be nice to find a solution that doesn't involve doing this, but whatever
					qe.nominalFace(face);
					qe.pos(0, (float)origin.x, (float)origin.y, (float)origin.z);
					qe.pos(1, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z);
					qe.pos(2, (float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z);
					qe.pos(3, (float)origin.x, (float)origin.y, (float)origin.z);
					for (int i = 0; i < 4; i++) {
						qe.normal(i, (float)plane.normal().x, (float)plane.normal().y, (float)plane.normal().z);
					}
					qe.spriteBake(0, sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
					qe.spriteColor(0, c, c, c, c);
					qe.emit();
				}
			}
		}
		Mesh mesh = bldr.build();
//		List<BakedQuad> quads = Lists.newArrayList();
//		mesh.forEach((qv) -> quads.add(qv.toBakedQuad(0, sprite, false)));
//		Map<Direction, List<BakedQuad>> directions = Maps.newHashMap();
//		for (Direction d : Direction.values()) {
//			// TODO determine if any polygons entirely touch a face so they can be culled
//			directions.put(d, Lists.newArrayList());
//		}
//		BasicBakedModel model = new BasicBakedModel(quads, directions, false, false, true, sprite, ModelTransformation.NONE, ModelOverrideList.EMPTY);
//		entity.clientCacheData = model;
		MinecraftClient.getInstance().getProfiler().pop();
		return mesh;
	}

	private static void buildTrivial(Sprite sprite, QuadEmitter qe, Polygon p, boolean invert) {
		Plane plane = p.plane();
		Direction face = findClosestFace(plane.normal());
		qe.nominalFace(face);
		if (invert) plane = new Plane(plane.normal().negate(), 0);
		int i = invert ? 3 : 0;
		for (DEdge de : p) {
			emit(sprite, qe, plane, de, i);
			i += (invert ? -1 : 1);
		}
		if (p.nPoints() == 3) emit(sprite, qe, plane, p.first(), i);
		qe.spriteBake(0, sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
		int c = -1;//p.nPoints() == 3 ? 0x00FFFF : 0x00FF00;
		qe.spriteColor(0, c, c, c, c);
		qe.emit();
	}

	private static void emit(Sprite sprite, QuadEmitter qe, Plane plane, DEdge de, int i) {
		qe.pos(i, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z);
		qe.normal(i, (float)plane.normal().x, (float)plane.normal().y, (float)plane.normal().z);
	}
	
	private static Direction findClosestFace(Vec3d normal) {
		return Direction.getFacing(normal.x, normal.y, normal.z);
	}
	
}
