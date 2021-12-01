package com.unascribed.yttr.inred;

import net.minecraft.util.StringIdentifiable;

public enum InactiveSelection implements StringIdentifiable {
	NONE("none"), LEFT("left"), BACK("back"), RIGHT("right");

	private final String name;

	InactiveSelection(String name) {
		this.name = name;
	}

	public static InactiveSelection forName(String s) {
		for (InactiveSelection value : InactiveSelection.values()) {
			if (s.equals(value.asString())) {
				return value;
			}
		}
		return InactiveSelection.NONE;
	}

	@Override
	public String asString() {
		return name;
	}
}
