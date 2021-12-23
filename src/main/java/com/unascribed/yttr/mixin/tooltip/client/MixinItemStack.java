package com.unascribed.yttr.mixin.tooltip.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class MixinItemStack {

	@Inject(at=@At(value="INVOKE", target="net/minecraft/item/Item.appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V"),
			method="getTooltip", locals=LocalCapture.CAPTURE_FAILHARD)
	public void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci, List<Text> tooltip) {
		ItemStack self = (ItemStack)(Object)this;
		if (Registry.ITEM.getId(self.getItem()).getNamespace().equals("yttr")) {
			int i = 1;
			while (I18n.hasTranslation(self.getTranslationKey()+".tip."+i)) {
				tooltip.add(new TranslatableText(self.getTranslationKey()+".tip."+i));
				i++;
			}
		}
	}
	
}
