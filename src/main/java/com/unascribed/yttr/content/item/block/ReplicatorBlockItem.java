package com.unascribed.yttr.content.item.block;

import java.util.List;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixin.accessor.AccessorDispenserBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

public class ReplicatorBlockItem extends BlockItem {

	public ReplicatorBlockItem(Block block, Settings settings) {
		super(block, settings);
		DispenserBlock.registerBehavior(this, (pointer, stack) -> {
			ItemStack inside = ReplicatorBlockItem.getHeldItem(stack);
			Block b = pointer.getBlockState().getBlock();
			if (b instanceof AccessorDispenserBlock) {
				((AccessorDispenserBlock)b).yttr$getBehaviorForItem(inside).dispense(pointer, inside);
			}
			return stack;
		});
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
	
	@Override
	protected SoundEvent getPlaceSound(BlockState state) {
		return YSounds.SILENCE;
	}
	
	public static ItemStack getHeldItem(ItemStack stack) {
		CompoundTag entityTag = stack.getSubTag("BlockEntityTag");
		if (entityTag != null) {
			return ItemStack.fromTag(entityTag.getCompound("Item"));
		}
		return ItemStack.EMPTY;
	}

}
