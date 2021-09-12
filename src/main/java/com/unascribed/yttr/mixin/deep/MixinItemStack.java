package com.unascribed.yttr.mixin.deep;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.inventory.HighStackGenericContainerScreenHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

	@Shadow
	public abstract Item getItem();
	
	@Inject(at=@At("RETURN"), method="getMaxCount", cancellable=true)
	public void getMaxCount(CallbackInfoReturnable<Integer> ci) {
		if (HighStackGenericContainerScreenHandler.increaseStackSize.get().intValue() > 0 && getItem().isIn(YTags.Item.ULTRAPURE_CUBES)) {
			ci.setReturnValue(1024);
		}
	}
	
}
