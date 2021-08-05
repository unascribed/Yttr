package com.unascribed.yttr.item.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class MercurialPotionItem extends PotionItem implements ItemColorProvider {

	public MercurialPotionItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		return new TranslatableText("item.yttr.mercurial_potion.prefix", Items.POTION.getName(stack));
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		return Items.POTION.getTranslationKey(stack);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		return tintIndex == 0 ? PotionUtil.getColor(stack) : -1;
	}

}
