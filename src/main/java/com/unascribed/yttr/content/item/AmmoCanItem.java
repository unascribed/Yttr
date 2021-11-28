package com.unascribed.yttr.content.item;

import java.util.List;

import com.unascribed.yttr.mechanics.rifle.RifleMode;

import com.google.common.base.Ascii;
import com.google.common.base.Enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class AmmoCanItem extends Item implements ItemColorProvider {

	public static final int CAPACITY = 1024;
	
	public AmmoCanItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if (!stack.hasTag()) return super.getName(stack);
		RifleMode mode = Enums.getIfPresent(RifleMode.class, stack.getTag().getString("Mode")).orNull();
		if (mode == null) return super.getName(stack);
		return new TranslatableText("item.yttr.ammo_can.prefixed", new TranslatableText("yttr.rifle_mode."+Ascii.toLowerCase(mode.name())));
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		int shots = stack.hasTag() ? stack.getTag().getInt("Shots") : 0;
		if (shots == 1) {
			tooltip.add(new TranslatableText("item.yttr.ammo_can.shots.one", CAPACITY).formatted(Formatting.GRAY));
		} else {
			tooltip.add(new TranslatableText("item.yttr.ammo_can.shots.many", shots, CAPACITY).formatted(Formatting.GRAY));
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex != 1) return -1;
		if (!stack.hasTag()) return -1;
		RifleMode mode = Enums.getIfPresent(RifleMode.class, stack.getTag().getString("Mode")).orNull();
		if (mode == null) return -1;
		return mode.color;
	}

}
