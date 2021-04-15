package com.unascribed.yttr.mixin.sorter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.block.abomination.SkeletalSorterBlockEntity;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(ChestBlockEntity.class)
public class MixinChestBlockEntity {

	@Inject(at=@At("RETURN"), method="tickViewerCount", cancellable=true)
	private static void tickViewerCount(World world, LockableContainerBlockEntity inventory, int ticksOpen, int x, int y, int z, int viewerCount, CallbackInfoReturnable<Integer> ci) {
		if (world.isClient) return;
		if (viewerCount == 0) {
			BlockPos.Mutable mut = new BlockPos.Mutable();
			for (Direction dir : Direction.Type.HORIZONTAL) {
				mut.set(x, y, z).move(dir);
				BlockEntity be = world.getBlockEntity(mut);
				if (be instanceof SkeletalSorterBlockEntity) {
					SkeletalSorterBlockEntity ssbe = (SkeletalSorterBlockEntity)be;
					if (ssbe.accessingInventory == dir.getOpposite()) {
						ci.setReturnValue(1);
						if (inventory instanceof ChestBlockEntity) {
							ChestBlockEntity cbe = (ChestBlockEntity)inventory;
							if (cbe.getCachedState().getBlock() instanceof ChestBlock) {
								world.addSyncedBlockEvent(cbe.getPos(), cbe.getCachedState().getBlock(), 1, 1);
								world.updateNeighborsAlways(cbe.getPos(), cbe.getCachedState().getBlock());
							}
						}
						return;
					}
				}
			}
		}
	}
	
}
