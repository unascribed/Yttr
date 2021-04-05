package com.unascribed.yttr.mixin.effector.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.EffectorWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

	private static BlockPos yttr$currentlyCollidingPos = null;
	
	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/util/math/BlockPos$Mutable.set(DDD)Lnet/minecraft/util/math/BlockPos$Mutable;"),
			method="getInWallBlockState", ordinal=0)
	private static BlockPos.Mutable storeMutable(BlockPos.Mutable mut) {
		yttr$currentlyCollidingPos = mut;
		return mut;
	}
	
	@Inject(at=@At("RETURN"), method="getInWallBlockState")
	private static void forgetMutable(PlayerEntity entity, CallbackInfoReturnable<BlockState> ci) {
		yttr$currentlyCollidingPos = null;
	}
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			ordinal=0, method="getInWallBlockState")
	private static BlockState replaceBlockState(BlockState in) {
		if (yttr$currentlyCollidingPos == null) return in;
		World world = MinecraftClient.getInstance().world;
		if (world instanceof EffectorWorld && ((EffectorWorld)world).yttr$isPhased(yttr$currentlyCollidingPos)) {
			return Blocks.VOID_AIR.getDefaultState();
		}
		return in;
	}
	
}
