package com.unascribed.yttr.content.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LevitationChamberBlockItem extends BlockItem implements Wearable {

	public LevitationChamberBlockItem(Block block, Settings settings) {
		super(block, settings);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(stack);
		ItemStack cur = user.getEquippedStack(slot);
		if (cur.isEmpty()) {
			user.equipStack(slot, stack.split(1));
			return TypedActionResult.success(stack, world.isClient());
		} else {
			return TypedActionResult.fail(stack);
		}
	}
	
}
