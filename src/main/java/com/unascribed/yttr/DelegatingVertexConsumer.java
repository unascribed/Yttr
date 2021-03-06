package com.unascribed.yttr;

import net.minecraft.client.render.VertexConsumer;

public class DelegatingVertexConsumer implements VertexConsumer {

	private final VertexConsumer delegate;
	
	public DelegatingVertexConsumer(VertexConsumer delegate) {
		this.delegate = delegate;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return delegate.vertex(x, y, z);
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return delegate.color(red, green, blue, alpha);
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		return delegate.texture(u, v);
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return delegate.overlay(u, v);
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return delegate.light(u, v);
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return delegate.normal(x, y, z);
	}

	@Override
	public void next() {
		delegate.next();
	}

}
