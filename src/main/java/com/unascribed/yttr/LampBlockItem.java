package com.unascribed.yttr;

import java.util.Locale;

import com.google.common.base.Enums;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class LampBlockItem extends BlockItem {

	public LampBlockItem(Block block, Settings settings) {
		super(block, settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		return new TranslatableText(getBlock().getTranslationKey()+"."+(LampBlockItem.isInverted(stack) ? "inverted" : "colored"),
				new TranslatableText("color.yttr."+LampBlockItem.getColor(stack).asString()));
	}

	public static LampColor getColor(ItemStack stack) {
		if (!stack.hasTag()) return LampColor.WHITE;
		return Enums.getIfPresent(LampColor.class, stack.getTag().getString("LampColor").toUpperCase(Locale.ROOT)).or(LampColor.WHITE);
	}

	public static boolean isInverted(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("Inverted");
	}

	public static void setInverted(ItemStack is, boolean inverted) {
		if (!is.hasTag()) is.setTag(new CompoundTag());
		is.getTag().putBoolean("Inverted", inverted);
	}
	
	public static void setColor(ItemStack is, LampColor color) {
		is.getTag().putString("LampColor", color.asString());
	}

}
