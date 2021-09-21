package com.unascribed.yttr.content.item;

import com.unascribed.yttr.world.WastelandPopulator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ShearsItem extends net.minecraft.item.ShearsItem {

	public ShearsItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (selected && entity.isSprinting() && RANDOM.nextInt(10) == 0) {
			entity.damage(new EntityDamageSource("yttr.scissors", entity), 4+RANDOM.nextInt(8));
		}
	}
	
	@Override
	public ActionResult useOnEntity(ItemStack in, PlayerEntity user, LivingEntity entity, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		ItemStack dummy = new ItemStack(Items.SHEARS);
		user.setStackInHand(hand, dummy);
		try {
			ActionResult ar = entity.interact(user, hand);
			stack.damage(dummy.getDamage(), user, (e) -> user.sendToolBreakStatus(hand));
			return ar;
		} finally {
			user.setStackInHand(hand, stack);
		}
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		WastelandPopulator.didYouKnowWeHaveVeinMiner(context.getWorld(), context.getBlockPos());
		return ActionResult.SUCCESS;
	}

}
