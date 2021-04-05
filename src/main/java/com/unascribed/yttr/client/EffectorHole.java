package com.unascribed.yttr.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EffectorHole {

	public final BlockPos start;
	public final Direction dir;
	public final int length;
	
	public int age;
	
	public EffectorHole(BlockPos start, Direction dir, int length) {
		this.start = start;
		this.dir = dir;
		this.length = length;
	}
	
	
	
}
