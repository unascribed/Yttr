package com.unascribed.yttr.item.block;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public class ReplicatorBlockItem extends BlockItem {

	public ReplicatorBlockItem(Block block, Settings settings) {
		super(block, settings);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		ItemStack held = getHeldItem(stack);
		if (held.isEmpty()) return;
		List<Text> inner = held.getTooltip(MinecraftClient.getInstance().player, context);
		for (int i = 0; i < inner.size(); i++) {
			tooltip.add(new LiteralText("  ").append(inner.get(i)));
		}
		tooltip.add(new LiteralText(""));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
	
	public static ItemStack getHeldItem(ItemStack stack) {
		CompoundTag entityTag = stack.getSubTag("BlockEntityTag");
		if (entityTag != null) {
			return ItemStack.fromTag(entityTag.getCompound("Item"));
		}
		return ItemStack.EMPTY;
	}

}
