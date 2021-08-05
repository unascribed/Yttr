package com.unascribed.yttr.mixin.effector;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldView;

@Mixin(VoxelShapes.class)
public class MixinVoxelShapes {

	private static BlockPos yttr$currentlyCheckingPos = null;
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/WorldView.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			ordinal=0, method="calculatePushVelocity(Lnet/minecraft/util/math/Box;Lnet/minecraft/world/WorldView;DLnet/minecraft/block/ShapeContext;Lnet/minecraft/util/math/AxisCycleDirection;Ljava/util/stream/Stream;)D")
	private static BlockState replaceBlockState(BlockState in, Box box, WorldView world) {
		if (world instanceof YttrWorld && yttr$currentlyCheckingPos != null && ((YttrWorld)world).yttr$isPhased(yttr$currentlyCheckingPos)) {
			return Blocks.VOID_AIR.getDefaultState();
		}
		return in;
	}
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/util/math/Box.getMin(Lnet/minecraft/util/math/Direction$Axis;)D"),
			method="calculatePushVelocity(Lnet/minecraft/util/math/Box;Lnet/minecraft/world/WorldView;DLnet/minecraft/block/ShapeContext;Lnet/minecraft/util/math/AxisCycleDirection;Ljava/util/stream/Stream;)D", locals=LocalCapture.CAPTURE_FAILHARD)
	private static void storeMutable(Box arg1, WorldView arg2, double arg3, ShapeContext arg4, AxisCycleDirection arg5, Stream<VoxelShape> arg6,
			CallbackInfoReturnable<Double> ci,
			AxisCycleDirection var1, Direction.Axis var2, Direction.Axis var3, Direction.Axis var4, BlockPos.Mutable mut) {
		yttr$currentlyCheckingPos = mut;
	}
	
	@Inject(at=@At("RETURN"), method="calculatePushVelocity(Lnet/minecraft/util/math/Box;Lnet/minecraft/world/WorldView;DLnet/minecraft/block/ShapeContext;Lnet/minecraft/util/math/AxisCycleDirection;Ljava/util/stream/Stream;)D")
	private static void forgetMutable(Box arg1, WorldView arg2, double arg3, ShapeContext arg4, AxisCycleDirection arg5, Stream<VoxelShape> arg6, CallbackInfoReturnable<Double> ci) {
		yttr$currentlyCheckingPos = null;
	}
	
}
