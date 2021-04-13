package com.unascribed.yttr.math;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;

public class Vec2i {

	public final int x;
	public final int z;
	
	public Vec2i(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public int squaredDistanceTo(Vec2i vec) {
		return squaredDistanceTo(vec.x, vec.z);
	}
	
	public int squaredDistanceTo(int x, int z) {
		int dx = this.x-x;
		int dz = this.z-z;
		return (dx*dx) + (dz*dz);
	}

	public IntArrayTag toTag() {
		return new IntArrayTag(new int[] {x, z});
	}
	
	public static @Nullable Vec2i fromTag(Tag tag) {
		if (!(tag instanceof IntArrayTag)) return null;
		int[] arr = ((IntArrayTag)tag).getIntArray();
		if (arr.length != 2) return null;
		return new Vec2i(arr[0], arr[1]);
	}
	
}
