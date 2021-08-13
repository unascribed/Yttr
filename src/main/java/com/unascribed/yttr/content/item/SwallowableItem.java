package com.unascribed.yttr.content.item;

import com.unascribed.yttr.init.YSounds;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SwallowableItem extends Item {

	public SwallowableItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return super.use(world, user, hand);
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity) {
			((PlayerEntity) user).getItemCooldownManager().set(this, 20*60);
		}
		return super.finishUsing(stack, world, user);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 5;
	}
	
	@Override
	public SoundEvent getEatSound() {
		return YSounds.SWALLOW;
	}

}
