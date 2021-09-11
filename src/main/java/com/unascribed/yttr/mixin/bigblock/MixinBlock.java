package com.unascribed.yttr.mixin.bigblock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.content.block.big.BigBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public class MixinBlock {

	@Inject(at=@At(value="INVOKE", target="net/minecraft/entity/ItemEntity.<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", shift=Shift.BY, by=2),
			method="dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V",
			locals=LocalCapture.CAPTURE_FAILHARD)
	private static void dropStack(World world, BlockPos pos, ItemStack stack, CallbackInfo ci, float f, double xO, double yO, double zO, ItemEntity entity) {
		BlockState bs = world.getBlockState(pos);
		if (bs.getBlock() instanceof BigBlock) {
			((BigBlock)bs.getBlock()).alterDroppedEntity(pos, bs, entity);
		}
	}
	
}
