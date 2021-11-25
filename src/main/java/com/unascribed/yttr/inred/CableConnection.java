package com.unascribed.yttr.inred;

import net.minecraft.util.StringIdentifiable;

public enum CableConnection implements StringIdentifiable {
	DISCONNECTED("disconnected"), CONNECTED("connected"), CONNECTED_UP("connected_up");

	private final String name;

	CableConnection(String name) {
		this.name=name;
	}

	@Override
	public String asString() {
		return name;
	}
}
