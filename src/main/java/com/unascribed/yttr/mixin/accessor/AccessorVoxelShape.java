package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

@Mixin(VoxelShape.class)
public interface AccessorVoxelShape {

	@Accessor("voxels")
	VoxelSet yttr$getVoxels();

	@Invoker("getPointPosition")
	double yttr$getPointPosition(Axis a, int i);
	@Invoker("getPointPositions")
	DoubleList yttr$getPointPositions(Axis a);
	@Invoker("getCoordIndex")
	int yttr$getCoordIndex(Axis a, double d);
	@Invoker("contains")
	boolean yttr$contains(double x, double y, double z);
	@Invoker("calculateMaxDistance")
	double yttr$calculateMaxDistance(AxisCycleDirection acd, Box b, double d);
	
}
