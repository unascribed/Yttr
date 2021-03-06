package com.unascribed.yttr.mixin.unbreakable_smithing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;

@Mixin(SmithingScreenHandler.class)
public abstract class MixinSmithingScreenHandler extends ForgingScreenHandler {

	public MixinSmithingScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}
	
	private boolean yttr$applyingBedrock = false;

	@Inject(at=@At("TAIL"), method="updateResult")
	public void updateResult(CallbackInfo ci) {
		yttr$applyingBedrock = false;
		if (output.isEmpty() && input.getStack(1).getItem() == YItems.BEDROCK_SHARD) {
			ItemStack in = input.getStack(0);
			if (!in.isDamageable() || (in.hasTag() && in.getTag().getBoolean("Unbreakable")) || in.getItem().isIn(YTags.Item.CANNOT_UNBREAKABLE)) return;
			ItemStack copy = in.copy();
			copy.setDamage(0);
			if (!copy.hasTag()) copy.setTag(new CompoundTag());
			copy.getTag().putBoolean("Unbreakable", true);
			output.setStack(0, copy);
			yttr$applyingBedrock = true;
		}
	}
	
	@Inject(at=@At("HEAD"), method="canTakeOutput", cancellable=true)
	protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> ci) {
		if (yttr$applyingBedrock) ci.setReturnValue(true);
	}
	
}
