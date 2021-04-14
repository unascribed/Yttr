package com.unascribed.yttr.mixin.effector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.mixinsupport.EffectorWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity {

	@Shadow
	public World world;
	
	@Shadow
	public abstract double getX();
	@Shadow
	public abstract double getEyeY();
	@Shadow
	public abstract double getZ();
	
	private BlockPos yttr$currentlyCollidingPos = null;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/world/World.isRegionLoaded(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Z"),
			method="checkBlockCollision", locals=LocalCapture.CAPTURE_FAILHARD)
	public void storeMutableForBlock(CallbackInfo ci, Box box, BlockPos start, BlockPos end, BlockPos.Mutable mut) {
		yttr$currentlyCollidingPos = mut;
	}
	
	@Inject(at=@At("RETURN"), method="checkBlockCollision")
	public void forgetMutableForBlock(CallbackInfo ci) {
		yttr$currentlyCollidingPos = null;
	}
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			ordinal=0, method="checkBlockCollision")
	public BlockState replaceBlockState(BlockState in) {
		if (yttr$currentlyCollidingPos == null) return in;
		if (world instanceof EffectorWorld && ((EffectorWorld)world).yttr$isPhased(yttr$currentlyCollidingPos)) {
			return Blocks.VOID_AIR.getDefaultState();
		}
		return in;
	}
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/World.getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"),
			ordinal=0, method="updateSubmergedInWaterState")
	public FluidState replaceFluidStateForSubmerge(FluidState in) {
		if (in.isEmpty()) return in;
		if (world instanceof EffectorWorld && ((EffectorWorld)world).yttr$isPhased(new BlockPos(getX(), getEyeY()-0.1111111119389534, getZ()))) {
			return Fluids.EMPTY.getDefaultState();
		}
		return in;
	}
	
	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/util/math/BlockPos$Mutable.set(III)Lnet/minecraft/util/math/BlockPos$Mutable;"),
			method="updateMovementInFluid", ordinal=0)
	public BlockPos.Mutable storeMutableForFluid(BlockPos.Mutable mut) {
		yttr$currentlyCollidingPos = mut;
		return mut;
	}
	
	@Inject(at=@At("RETURN"), method="updateMovementInFluid")
	public void forgetMutableForFluid(Tag<Fluid> tag, double d, CallbackInfoReturnable<Boolean> ci) {
		yttr$currentlyCollidingPos = null;
	}
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/World.getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"),
			ordinal=0, method="updateMovementInFluid")
	public FluidState replaceFluidStateForTouch(FluidState in) {
		if (in.isEmpty()) return in;
		if (world instanceof EffectorWorld && ((EffectorWorld)world).yttr$isPhased(yttr$currentlyCollidingPos)) {
			return Fluids.EMPTY.getDefaultState();
		}
		return in;
	}
	
}
