package com.unascribed.yttr.client.util;

import net.minecraft.client.render.VertexConsumer;

public class VertexObserver implements VertexConsumer {
	
	private int count;
	private double minX = Float.POSITIVE_INFINITY;
	private double maxX = Float.NEGATIVE_INFINITY;
	private double minY = Float.POSITIVE_INFINITY;
	private double maxY = Float.NEGATIVE_INFINITY;
	private double minZ = Float.POSITIVE_INFINITY;
	private double maxZ = Float.NEGATIVE_INFINITY;
	
	public int getCount() {
		return count;
	}
	
	public double getMinX() {
		return minX;
	}
	public double getMaxX() {
		return maxX;
	}
	
	public double getMinY() {
		return minY;
	}
	public double getMaxY() {
		return maxY;
	}
	
	public double getMinZ() {
		return minZ;
	}
	public double getMaxZ() {
		return maxZ;
	}
	
	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		minZ = Math.min(minZ, z);
		maxZ = Math.max(maxZ, z);
		return this;
	}
	
	@Override
	public VertexConsumer texture(float u, float v) {
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
		count++;
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
