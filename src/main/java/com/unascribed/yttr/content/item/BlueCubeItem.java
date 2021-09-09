package com.unascribed.yttr.content.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BlueCubeItem extends Item {

	private static final int[] NOTES = {
			5, 0, 8, 20, 4, 5, 3, 8, 10, 20, 5, 10, 22, 5, 1, 3
	};
	
	public BlueCubeItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 20000;
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient) return;
		int speed = 3;
		int t = getMaxUseTime(stack)-remainingUseTicks;
		if (t%speed != 0) return;
		int i = t/speed;
		float vol = 1;
		if (i < 40) {
			vol = i/40f;
		}
		int n = NOTES[i%NOTES.length];
		float pitch = (float)Math.pow(2, (n - 12) / 12.0);
		user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, vol, pitch);
	}

}
