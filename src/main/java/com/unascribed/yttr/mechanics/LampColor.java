package com.unascribed.yttr.mechanics;

import java.util.Locale;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.mixin.accessor.AccessorDyeColor;
import com.unascribed.yttr.util.Resolvable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

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
	
	TEAL("yttr:yttrium_dust", 0x1ABC9A, 0x00DB8D),
	COLORLESS(0xFFF3E0, 0xFFB74D),
	;
	
	public static final ImmutableList<LampColor> VALUES = ImmutableList.copyOf(values());
	public static final ImmutableMap<DyeColor, LampColor> BY_DYE = ImmutableMap.copyOf(VALUES.stream()
			.filter(lc -> lc.dyeColor != null).map(lc -> Maps.immutableEntry(lc.dyeColor, lc))
			.collect(Collectors.toList()));
	public static final ImmutableMap<Resolvable<Item>, LampColor> BY_ITEM = ImmutableMap.copyOf(VALUES.stream()
			.filter(lc -> lc.item != null).map(lc -> Maps.immutableEntry(lc.item, lc))
			.collect(Collectors.toList()));
	
	public final @Nullable DyeColor dyeColor;
	public final @Nullable Resolvable<Item> item;
	public final int baseLitColor;
	public final int baseUnlitColor;
	public final int glowColor;
	
	private final String lowerName;
	
	LampColor(DyeColor inherit) {
		this(inherit, ((AccessorDyeColor)(Object)inherit).yttr$getSignColor());
	}
	
	LampColor(DyeColor inherit, int glowColor) {
		this(inherit, null, ((AccessorDyeColor)(Object)inherit).yttr$getColor(), glowColor);
	}
	
	LampColor(String item, int baseColor, int glowColor) {
		this(null, item, baseColor, glowColor);
	}
	
	LampColor(int baseColor, int glowColor) {
		this(null, null, baseColor, glowColor);
	}
	
	LampColor(DyeColor dyeColor, String item, int baseColor, int glowColor) {
		this.dyeColor = dyeColor;
		this.item = item == null ? null : Resolvable.of(new Identifier(item), Registry.ITEM);
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
