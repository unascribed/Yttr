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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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
		NbtCompound entityTag = stack.getSubTag("BlockEntityTag");
		if (entityTag != null) {
			return ItemStack.fromNbt(entityTag.getCompound("Item"));
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		ItemStack held = getHeldItem(stack);
		if (held.isFood() && user.canConsume(held.getItem().getFoodComponent().isAlwaysEdible())) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		ItemStack held = getHeldItem(stack);
		return held.getMaxUseTime();
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		ItemStack held = getHeldItem(stack);
		return held.getUseAction();
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack held = getHeldItem(stack);
		held.copy().finishUsing(world, user);
		return stack;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		ItemStack held = getHeldItem(stack);
		held.copy().onStoppedUsing(world, user, remainingUseTicks);
	}
	
}
