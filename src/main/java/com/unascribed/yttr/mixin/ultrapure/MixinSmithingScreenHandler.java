package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.unascribed.yttr.init.YItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

@Mixin(SmithingScreenHandler.class)
public abstract class MixinSmithingScreenHandler extends ForgingScreenHandler {

	public MixinSmithingScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(at=@At("TAIL"), method="updateResult")
	public void updateResult(CallbackInfo ci) {
		if (!output.isEmpty() && input.getStack(1).getItem() == YItems.ULTRAPURE_NETHERITE) {
			ItemStack out = output.getStack(0);
			if (!out.hasCustomName()) {
				out.setCustomName(new TranslatableText("item.yttr.ultrapure_tool.prefix", out.getName()).setStyle(Style.EMPTY.withItalic(false)));
			}
			if (!out.hasTag()) {
				out.setTag(new CompoundTag());
			}
			out.getTag().putInt("yttr:DurabilityBonus", out.getTag().getInt("yttr:DurabilityBonus")+1);
		}
	}
	
}
