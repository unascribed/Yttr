package com.unascribed.yttr.mixin.replicator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.init.YItems;

import net.minecraft.block.DropperBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;

@Mixin(DropperBlock.class)
public class MixinDropperBlock {

	private ItemStack yttr$savedStack = null;
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/block/entity/DispenserBlockEntity.getStack(I)Lnet/minecraft/item/ItemStack;"), method="dispense")
	public ItemStack modifyDispensedStack(ItemStack in) {
		yttr$savedStack = null;
		if (in.getItem() == YItems.REPLICATOR) {
			ItemStack inside = ReplicatorBlockItem.getHeldItem(in);
			yttr$savedStack = in;
			return inside;
		}
		return in;
	}
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/block/entity/DispenserBlockEntity.setStack(ILnet/minecraft/item/ItemStack;)V", shift=Shift.AFTER),
			method="dispense", locals=LocalCapture.CAPTURE_FAILHARD)
	public void afterSetStack(ServerWorld serverWorld, BlockPos pos, CallbackInfo ci, BlockPointerImpl ptr, DispenserBlockEntity dbe, int slot) {
		if (yttr$savedStack != null) {
			dbe.setStack(slot, yttr$savedStack);
			yttr$savedStack = null;
		}
	}
	
}
