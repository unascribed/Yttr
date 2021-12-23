package com.unascribed.yttr.mixin.deep;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.inventory.DSUScreenHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

	@Shadow
	public abstract Item getItem();
	
	@Inject(at=@At("RETURN"), method="getMaxCount", cancellable=true)
	public void getMaxCount(CallbackInfoReturnable<Integer> ci) {
		if (DSUScreenHandler.increaseStackSize.get().intValue() > 0) {
			if (getItem().isIn(YTags.Item.DSU_4096)) {
				ci.setReturnValue(4096);
			} else if (getItem().isIn(YTags.Item.DSU_2048)) {
				ci.setReturnValue(2048);
			} else if (getItem().isIn(YTags.Item.DSU_1024)) {
				ci.setReturnValue(1024);
			} else if (getItem().isIn(YTags.Item.DSU_512)) {
				ci.setReturnValue(512);
			}
		}
	}
	
}
