package com.unascribed.yttr.mixin.smashing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.mechanics.SmashCloudLogic;

import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;

@Mixin(targets="net/minecraft/block/dispenser/DispenserBehavior$17")
public abstract class MixinGlassBottleDispenserBehavior extends FallibleItemDispenserBehavior {

	@Inject(at=@At("HEAD"), method="dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", cancellable=true)
	public void dispenseSilently(BlockPointer ptr, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
		PistonSmashingRecipe r = SmashCloudLogic.consumeGasCloud(ptr.getWorld(), new Box(ptr.getBlockPos()).expand(0.5));
		if (r != null) {
			setSuccess(true);
			ci.setReturnValue(method_22141(ptr, stack, r.getCloudOutput().copy()));
		}
	}
	
	@Shadow
	private ItemStack method_22141(BlockPointer var1, ItemStack var2, ItemStack var3) { return null; }
	
}
