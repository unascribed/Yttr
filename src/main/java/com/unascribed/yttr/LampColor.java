package com.unascribed.yttr;

import java.util.Locale;

import com.unascribed.yttr.mixin.AccessorDyeColor;

import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;

public enum LampColor implements StringIdentifiable {
	WHITE(DyeColor.WHITE),
	ORANGE(DyeColor.ORANGE),
	MAGENTA(DyeColor.MAGENTA),
	LIGHT_BLUE(DyeColor.LIGHT_BLUE),
	YELLOW(DyeColor.YELLOW),
	LIME(DyeColor.LIME),
	PINK(DyeColor.PINK),
	GRAY(DyeColor.GRAY),
	LIGHT_GRAY(DyeColor.LIGHT_GRAY),
	CYAN(DyeColor.CYAN),
	PURPLE(DyeColor.PURPLE),
	BLUE(DyeColor.BLUE),
	BROWN(DyeColor.BROWN),
	GREEN(DyeColor.GREEN),
	RED(DyeColor.RED),
	BLACK(DyeColor.BLACK, 0x222222),
	
	TEAL(0x1ABC9A, 0x00DBAD)
	;
	
	public final int baseLitColor;
	public final int baseUnlitColor;
	public final int glowColor;
	
	private final String lowerName;
	
	LampColor(DyeColor inherit) {
		this(inherit, inherit.getSignColor());
	}
	
	LampColor(DyeColor inherit, int glowColor) {
		this(((AccessorDyeColor)(Object)inherit).yttr$getColor(), glowColor);
	}
	
	LampColor(int baseColor, int glowColor) {
		this.baseLitColor = baseColor;
		this.glowColor = glowColor;
		
		int r = (baseColor >> 16) & 0xFF;
		int g = (baseColor >> 8) & 0xFF;
		int b = baseColor & 0xFF;
		r /= 2;
		g /= 2;
		b /= 2;
		this.baseUnlitColor = (r << 16) | (g << 8) | b;
		
		this.lowerName = name().toLowerCase(Locale.ROOT);
	}

	@Override
	public String asString() {
		return lowerName;
	}
}
