package com.unascribed.yttr.content.item;

import java.util.Set;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DropOfContinuityItem extends Item {

	public DropOfContinuityItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 170;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isCreative()) {
			finishUsing(user.getStackInHand(hand), world, user);
		} else {
			user.setCurrentHand(hand);
			if (!world.isClient) {
				world.playSoundFromEntity(null, user, YSounds.DROP_CAST, SoundCategory.PLAYERS, 1, 1);
			}
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if (world instanceof ServerWorld) {
			Box box = user.getBoundingBox();
			Vec3d center = box.getCenter();
			if (RANDOM.nextInt(remainingUseTicks) < 40) {
				int m = 1;
				if (remainingUseTicks < 20) {
					m = 4;
				} else if (remainingUseTicks < 40) {
					m = 2;
				}
				((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, center.x, center.y, center.z, 1*m, box.getXLength()/3, box.getYLength()/3, box.getZLength()/3, 0.05);
				float f = RANDOM.nextFloat()/2;
				((ServerWorld)world).spawnParticles(new DustParticleEffect(1, 0.75f-f, 0.5f, 0.5f), center.x, center.y, center.z, 3*m, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0);
			}
		}
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		super.onStoppedUsing(stack, world, user, remainingUseTicks);
		if (!world.isClient) {
			world.playSoundFromEntity(null, user, YSounds.DROP_CAST_CANCEL, SoundCategory.PLAYERS, 1, 1);
			if ((getMaxUseTime(stack)-remainingUseTicks) > 15) {
				world.playSoundFromEntity(null, user, YSounds.DROP_CAST_CANCEL_AUDIBLE, SoundCategory.PLAYERS, 1, 1);
			}
		}
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient) {
			Set<Item> possibilities = Sets.newHashSet();
			possibilities.addAll(YTags.Item.GIFTS.values());
			possibilities.addAll(Collections2.transform(YTags.Block.GIFTS.values(), Block::asItem));
			possibilities.removeAll(YTags.Item.NOT_GIFTS.values());
			possibilities.remove(null);
			ItemStack gift = new ItemStack(Iterables.get(possibilities, RANDOM.nextInt(possibilities.size())));
			gift.setCount(Math.min(gift.getMaxCount(), RANDOM.nextInt(3)+1));
			if (user.isUsingItem()) {
				user.setStackInHand(user.getActiveHand(), gift);
			} else if (user instanceof PlayerEntity && ((PlayerEntity) user).isCreative()) {
				((PlayerEntity) user).inventory.offerOrDrop(world, gift);
			} else {
				user.dropStack(gift);
				stack.setCount(0);
			}
		}
		if (world instanceof ServerWorld) {
			Box box = user.getBoundingBox();
			Vec3d center = box.getCenter();
			((ServerWorld)world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), center.x, center.y, center.z, 30, 0.25, 0.25, 0.25, 0.05);
			((ServerWorld)world).spawnParticles(new DustParticleEffect(1, 0.75f, 0.5f, 0.5f), center.x, center.y, center.z, 10, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0.0125);
			for (int i = 0; i < 50; i++) {
				((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 0, RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian(), 0.25);
				((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, center.x, center.y, center.z, 0, RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian(), 0.25);
			}
		}
		return super.finishUsing(stack, world, user);
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}
	
	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
	
}
