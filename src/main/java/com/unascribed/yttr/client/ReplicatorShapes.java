package com.unascribed.yttr.client;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.unascribed.yttr.client.util.VertexObserver;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class ReplicatorShapes {
	
	public static final VertexFormat POSITION_NORMAL = new VertexFormat(ImmutableList.of(VertexFormats.POSITION_ELEMENT, VertexFormats.NORMAL_ELEMENT));

	public static final VertexBuffer OCTAHEDRON = build(ReplicatorShapes::octahedron);
	public static final VertexBuffer DODECAHEDRON = build(ReplicatorShapes::dodecahedron);
	public static final VertexBuffer ICOSAHEDRON = build(ReplicatorShapes::icosahedron);
	public static final ImmutableList<VertexBuffer> ALL = ImmutableList.of(OCTAHEDRON, DODECAHEDRON, ICOSAHEDRON);

	public static VertexBuffer build(BiConsumer<MatrixStack, VertexConsumer> cons) {
		VertexObserver vo = new VertexObserver();
		MatrixStack mat = new MatrixStack();
		cons.accept(mat, vo);
		double dX = vo.getMaxX()-vo.getMinX();
		double dY = vo.getMaxY()-vo.getMinY();
		double dZ = vo.getMaxZ()-vo.getMinZ();
		float scale = (float)(1/Doubles.max(dX, dY, dZ));
		mat.scale(scale, scale, scale);
		BufferBuilder bb = new BufferBuilder(vo.getCount()*POSITION_NORMAL.getVertexSizeInteger());
		bb.begin(GL11.GL_TRIANGLES, POSITION_NORMAL);
		cons.accept(mat, bb);
		bb.end();
		VertexBuffer out = new VertexBuffer(POSITION_NORMAL);
		out.upload(bb);
		return out;
	}
	
	public static void octahedron(MatrixStack m, VertexConsumer vc) {
		Matrix4f mod = m.peek().getModel();
		Matrix3f nrm = m.peek().getNormal();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, 0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).normal(nrm, 0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).normal(nrm, 0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, -0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).normal(nrm, -0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).normal(nrm, -0.577350f, 0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, -0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).normal(nrm, -0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).normal(nrm, -0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, 0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).normal(nrm, 0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).normal(nrm, 0.577350f, -0.577350f, 0.577350f).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).normal(nrm, 0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).normal(nrm, 0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, 0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).normal(nrm, -0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).normal(nrm, -0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, -0.577350f, 0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).normal(nrm, -0.577350f, -0.577350f, -0.577350f).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).normal(nrm, -0.577350f, -0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, -0.577350f, -0.577350f, -0.577350f).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).normal(nrm, 0.577350f, -0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).normal(nrm, 0.577350f, -0.577350f, -0.577350f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, 0.577350f, -0.577350f, -0.577350f).next();
	}
	
	public static void dodecahedron(MatrixStack m, VertexConsumer vc) {
		Matrix4f mod = m.peek().getModel();
		Matrix3f nrm = m.peek().getNormal();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).normal(nrm, 0.000000f, 0.000000f, 1.000000f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.724111f, 0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).normal(nrm, 0.724111f, 0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, 0.724111f, 0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.723780f, 0.525399f, 0.447324f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, 0.723780f, 0.525399f, 0.447324f).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).normal(nrm, 0.723780f, 0.525399f, 0.447324f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.723766f, 0.525577f, 0.447137f).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).normal(nrm, 0.723766f, 0.525577f, 0.447137f).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.723766f, 0.525577f, 0.447137f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, -0.276379f, 0.850814f, 0.446911f).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).normal(nrm, -0.276379f, 0.850814f, 0.446911f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.276379f, 0.850814f, 0.446911f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, -0.276395f, 0.850862f, 0.446810f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.276395f, 0.850862f, 0.446810f).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).normal(nrm, -0.276395f, 0.850862f, 0.446810f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, -0.275624f, 0.850675f, 0.447642f).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).normal(nrm, -0.275624f, 0.850675f, 0.447642f).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).normal(nrm, -0.275624f, 0.850675f, 0.447642f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.895378f, 0.000000f, 0.445307f).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).normal(nrm, -0.895378f, 0.000000f, 0.445307f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.895378f, 0.000000f, 0.445307f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).normal(nrm, -0.894132f, 0.000000f, 0.447803f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.276379f, -0.850814f, 0.446911f).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).normal(nrm, -0.276379f, -0.850814f, 0.446911f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, -0.276379f, -0.850814f, 0.446911f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.276297f, -0.850560f, 0.447446f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, -0.276297f, -0.850560f, 0.447446f).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).normal(nrm, -0.276297f, -0.850560f, 0.447446f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.275782f, -0.851164f, 0.446613f).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).normal(nrm, -0.275782f, -0.851164f, 0.446613f).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).normal(nrm, -0.275782f, -0.851164f, 0.446613f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, 0.724111f, -0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).normal(nrm, 0.724111f, -0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.724111f, -0.525640f, 0.446504f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, 0.723838f, -0.525441f, 0.447181f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.723837f, -0.525441f, 0.447181f).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.723837f, -0.525441f, 0.447181f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, 0.723672f, -0.525509f, 0.447369f).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).normal(nrm, 0.723672f, -0.525509f, 0.447369f).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).normal(nrm, 0.723672f, -0.525509f, 0.447369f).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).normal(nrm, 0.275624f, 0.850675f, -0.447642f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, 0.275624f, 0.850675f, -0.447642f).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).normal(nrm, 0.275624f, 0.850675f, -0.447642f).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).normal(nrm, 0.276348f, 0.850851f, -0.446861f).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).normal(nrm, 0.276348f, 0.850851f, -0.446861f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, 0.276348f, 0.850851f, -0.446861f).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).normal(nrm, 0.276455f, 0.850833f, -0.446829f).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).normal(nrm, 0.276455f, 0.850833f, -0.446829f).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).normal(nrm, 0.276455f, 0.850833f, -0.446829f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, -0.723766f, 0.525577f, -0.447137f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, -0.723766f, 0.525577f, -0.447137f).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).normal(nrm, -0.723766f, 0.525577f, -0.447137f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, -0.723750f, 0.525790f, -0.446914f).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).normal(nrm, -0.723750f, 0.525790f, -0.446914f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.723750f, 0.525790f, -0.446914f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, -0.724160f, 0.525008f, -0.447167f).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).normal(nrm, -0.724160f, 0.525008f, -0.447167f).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).normal(nrm, -0.724160f, 0.525008f, -0.447167f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, -0.723672f, -0.525509f, -0.447369f).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).normal(nrm, -0.723672f, -0.525509f, -0.447369f).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).normal(nrm, -0.723672f, -0.525509f, -0.447369f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, -0.724136f, -0.525317f, -0.446842f).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).normal(nrm, -0.724136f, -0.525318f, -0.446842f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.724136f, -0.525317f, -0.446842f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, -0.723628f, -0.525840f, -0.447052f).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).normal(nrm, -0.723628f, -0.525840f, -0.447052f).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).normal(nrm, -0.723628f, -0.525840f, -0.447052f).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).normal(nrm, 0.275782f, -0.851164f, -0.446613f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.275782f, -0.851164f, -0.446613f).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).normal(nrm, 0.275782f, -0.851164f, -0.446613f).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).normal(nrm, 0.276131f, -0.850755f, -0.447178f).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).normal(nrm, 0.276131f, -0.850755f, -0.447178f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, 0.276131f, -0.850755f, -0.447178f).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).normal(nrm, 0.276647f, -0.850500f, -0.447344f).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).normal(nrm, 0.276647f, -0.850500f, -0.447344f).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).normal(nrm, 0.276647f, -0.850500f, -0.447344f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.894132f, 0.000000f, -0.447803f).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).normal(nrm, 0.894132f, 0.000000f, -0.447803f).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).normal(nrm, 0.894132f, 0.000000f, -0.447803f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.894756f, -0.001014f, -0.446554f).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).normal(nrm, 0.894756f, -0.001014f, -0.446555f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.894756f, -0.001014f, -0.446554f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.894369f, 0.001642f, -0.447326f).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).normal(nrm, 0.894369f, 0.001642f, -0.447326f).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).normal(nrm, 0.894369f, 0.001642f, -0.447326f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).normal(nrm, 0.000000f, 0.000000f, -1.000000f).next();
	}
	
	public static void icosahedron(MatrixStack m, VertexConsumer vc) {
		Matrix4f mod = m.peek().getModel();
		Matrix3f nrm = m.peek().getNormal();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, 0.491421f, 0.356872f, 0.794448f).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).normal(nrm, 0.491421f, 0.356872f, 0.794448f).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).normal(nrm, 0.491421f, 0.356872f, 0.794448f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, -0.187612f, 0.577268f, 0.794709f).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).normal(nrm, -0.187612f, 0.577268f, 0.794710f).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).normal(nrm, -0.187612f, 0.577268f, 0.794710f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, -0.607002f, 0.000000f, 0.794700f).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).normal(nrm, -0.607002f, 0.000000f, 0.794700f).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).normal(nrm, -0.607002f, 0.000000f, 0.794700f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, -0.187612f, -0.577268f, 0.794709f).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).normal(nrm, -0.187612f, -0.577268f, 0.794710f).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).normal(nrm, -0.187612f, -0.577268f, 0.794710f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).normal(nrm, 0.491421f, -0.356872f, 0.794448f).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).normal(nrm, 0.491421f, -0.356872f, 0.794448f).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).normal(nrm, 0.491421f, -0.356872f, 0.794448f).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).normal(nrm, 0.187612f, 0.577268f, -0.794710f).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).normal(nrm, 0.187612f, 0.577268f, -0.794710f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, 0.187612f, 0.577268f, -0.794709f).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).normal(nrm, -0.491421f, 0.356872f, -0.794448f).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).normal(nrm, -0.491421f, 0.356872f, -0.794448f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, -0.491421f, 0.356872f, -0.794448f).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).normal(nrm, -0.491421f, -0.356872f, -0.794448f).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).normal(nrm, -0.491421f, -0.356872f, -0.794448f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, -0.491421f, -0.356872f, -0.794448f).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).normal(nrm, 0.187612f, -0.577268f, -0.794710f).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).normal(nrm, 0.187612f, -0.577268f, -0.794710f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, 0.187612f, -0.577268f, -0.794709f).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).normal(nrm, 0.607002f, 0.000000f, -0.794700f).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).normal(nrm, 0.607002f, 0.000000f, -0.794700f).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).normal(nrm, 0.607002f, 0.000000f, -0.794700f).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).normal(nrm, 0.794653f, 0.577081f, 0.188427f).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).normal(nrm, 0.794653f, 0.577081f, 0.188427f).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).normal(nrm, 0.794653f, 0.577081f, 0.188427f).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).normal(nrm, -0.303607f, 0.934174f, 0.187462f).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).normal(nrm, -0.303607f, 0.934174f, 0.187462f).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).normal(nrm, -0.303607f, 0.934174f, 0.187462f).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).normal(nrm, -0.982396f, 0.000000f, 0.186809f).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).normal(nrm, -0.982396f, 0.000000f, 0.186809f).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).normal(nrm, -0.982396f, 0.000000f, 0.186809f).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).normal(nrm, -0.303607f, -0.934174f, 0.187462f).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).normal(nrm, -0.303607f, -0.934174f, 0.187462f).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).normal(nrm, -0.303607f, -0.934174f, 0.187462f).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).normal(nrm, 0.794653f, -0.577081f, 0.188427f).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).normal(nrm, 0.794653f, -0.577081f, 0.188427f).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).normal(nrm, 0.794653f, -0.577081f, 0.188427f).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).normal(nrm, 0.303607f, 0.934174f, -0.187462f).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).normal(nrm, 0.303607f, 0.934174f, -0.187462f).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).normal(nrm, 0.303607f, 0.934174f, -0.187462f).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).normal(nrm, -0.794653f, 0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).normal(nrm, -0.794653f, 0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).normal(nrm, -0.794653f, 0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).normal(nrm, -0.794653f, -0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).normal(nrm, -0.794653f, -0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).normal(nrm, -0.794653f, -0.577081f, -0.188427f).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).normal(nrm, 0.303607f, -0.934174f, -0.187462f).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).normal(nrm, 0.303607f, -0.934174f, -0.187462f).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).normal(nrm, 0.303607f, -0.934174f, -0.187462f).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).normal(nrm, 0.982396f, 0.000000f, -0.186809f).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).normal(nrm, 0.982396f, 0.000000f, -0.186809f).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).normal(nrm, 0.982396f, 0.000000f, -0.186809f).next();
	}
	
	public static void octahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		Matrix4f mod = m.peek().getModel();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 1.000000f, 0.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, -1.000000f, 0.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
	}
	
	public static void dodecahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		Matrix4f mod = m.peek().getModel();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).color(r, g, b, a).next();
		
		
		
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.188000f, 0.577000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.607000f, 0.000000f, 0.795000f).color(r, g, b, a).next();
		
		
		
		vc.vertex(mod, 0.304000f, 0.934000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.491000f, 0.357000f, 0.795000f).color(r, g, b, a).next();
		
		
		vc.vertex(mod, -0.795000f, 0.577000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.491000f, -0.357000f, 0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.795000f, -0.577000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.188000f, -0.577000f, 0.795000f).color(r, g, b, a).next();
		
		
		vc.vertex(mod, 0.304000f, -0.934000f, 0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.982000f, 0.000000f, 0.188000f).color(r, g, b, a).next();
		
		
		
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.304000f, 0.934000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.795000f, 0.577000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.188000f, 0.577000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.982000f, 0.000000f, -0.188000f).color(r, g, b, a).next();
		
		
		
		vc.vertex(mod, -0.607000f, 0.000000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.304000f, -0.934000f, -0.188000f).color(r, g, b, a).next();
		
		
		
		vc.vertex(mod, -0.188000f, -0.577000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.795000f, -0.577000f, -0.188000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.491000f, -0.357000f, -0.795000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.491000f, 0.357000f, -0.795000f).color(r, g, b, a).next();
	}
	
	public static void icosahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		Matrix4f mod = m.peek().getModel();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, 1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.000000f, 0.000000f, -1.000000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, 0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, 0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.894000f, 0.000000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, 0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, -0.276000f, -0.851000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, -0.724000f, -0.526000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, -0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.276000f, -0.851000f, 0.447000f).color(r, g, b, a).next();
		
		vc.vertex(mod, 0.724000f, 0.526000f, -0.447000f).color(r, g, b, a).next();
		vc.vertex(mod, 0.894000f, 0.000000f, 0.447000f).color(r, g, b, a).next();
	}
	

}
