package com.unascribed.yttr.mechanics;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

/**
 * Implement on a BlockEntity to opt into Yttr halo rendering.
 * See assets/yttr/models/block/lamp.json
 */
public interface HaloBlockEntity {

	boolean shouldRenderHalo();
	int getGlowColor();
	default @Nullable Direction getFacing() { return null; }
	Object getStateObject();
	
}
