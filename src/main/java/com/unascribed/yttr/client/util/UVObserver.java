package com.unascribed.yttr.client.util;

import net.minecraft.client.render.VertexConsumer;

public class UVObserver implements VertexConsumer {
	
	private float minU = 2;
	private float maxU = -2;
	private float minV = 2;
	private float maxV = -2;
	
	public float getMinU() {
		return minU;
	}
	public float getMaxU() {
		return maxU;
	}
	public float getMinV() {
		return minV;
	}
	public float getMaxV() {
		return maxV;
	}
	
	public void reset() {
		minU = minV = 2;
		maxU = maxV = -2;
	}
	
	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return this;
	}
	
	@Override
	public VertexConsumer texture(float u, float v) {
		minU = Math.min(minU, u);
		maxU = Math.max(maxU, u);
		minV = Math.min(minV, v);
		maxV = Math.max(maxV, v);
		return this;
	}
	
	@Override
	public VertexConsumer overlay(int u, int v) {
		return this;
	}
	
	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return this;
	}
	
	@Override
	public void next() {
	}
	
	@Override
	public VertexConsumer light(int u, int v) {
		return this;
	}
	
	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return this;
	}
}
