package com.unascribed.yttr;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;

public enum RifleMode {
	DAMAGE(Formatting.RED, 0xFF0000, () -> Items.REDSTONE, 12, 2) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (hit instanceof EntityHitResult) {
				int damage = (int)Math.ceil(power*14);
				((EntityHitResult) hit).getEntity().damage(new EntityDamageSource("yttr.rifle", user), damage);
			}
			if (hit instanceof BlockHitResult) {
				BlockHitResult bhr = (BlockHitResult)hit;
				BlockState bs = user.world.getBlockState(bhr.getBlockPos());
				if (bs.getBlock() == Yttr.POWER_METER) {
					if (bhr.getSide() == Direction.UP || bhr.getSide() == bs.get(PowerMeterBlock.FACING)) {
						BlockEntity be = user.world.getBlockEntity(bhr.getBlockPos());
						if (be instanceof PowerMeterBlockEntity) {
							((PowerMeterBlockEntity)be).sendReadout((int)(power*500));
						}
					}
				}
			}
			if (power > 1.2f) {
				user.world.createExplosion(null, DamageSource.explosion(user), null, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, false, DestructionType.NONE);
			}
		}
	},
	EXPLODE(Formatting.GRAY, 0xAAAAAA, () -> Items.GUNPOWDER, 1, 1) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			user.world.createExplosion(null, DamageSource.explosion(user), null, hit.getPos().x, hit.getPos().y, hit.getPos().z, power > 1.2 ? 5 : 3*power, power > 1.2, power > 1.2 ? DestructionType.DESTROY : DestructionType.BREAK);
		}
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			checkBedrock(user.world, user.getBlockPos().down());
			checkBedrock(user.world, user.getBlockPos().up(2));
			user.world.createExplosion(null, DamageSource.explosion(user), null, user.getPos().x, user.getPos().y, user.getPos().z, 5.5f, false, DestructionType.DESTROY);
		}
		void checkBedrock(World world, BlockPos pos) {
			if (pos.getY() == 0 || pos.getY() == 127 || pos.getY() == 255) return;
			BlockState bs = world.getBlockState(pos);
			if (bs.getBlock() == Blocks.BEDROCK) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				world.syncWorldEvent(2001, pos, Block.getRawIdFromState(bs));
			}
		}
	},
	TELEPORT(Formatting.LIGHT_PURPLE, 0xFF00FF, () -> Items.CHORUS_FRUIT, 3, 1.5f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (hit.getType() == Type.MISS) return;
			if (power > 1.1f) {
				user.world.createExplosion(user, user.getPos().x, user.getPos().y, user.getPos().z, 1*power, DestructionType.NONE);
			}
			user.teleport(hit.getPos().x, hit.getPos().y, hit.getPos().z);
			if (power > 1.2f) {
				user.damage(DamageSource.explosion(user), 4);
				user.world.createExplosion(user, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, DestructionType.NONE);
			}
		}
		
		@Override
		public boolean canFire(LivingEntity user, ItemStack stack, float power) {
			return power >= 0.8f;
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			for (int i = 0; i < 4; i++) {
				Items.CHORUS_FRUIT.finishUsing(new ItemStack(Items.CHORUS_FRUIT), user.world, user);
			}
		}
	},
	FIRE(Formatting.GOLD, 0xFFAA00, () -> Items.BLAZE_POWDER, 2, 2) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (hit instanceof EntityHitResult) {
				Entity e = ((EntityHitResult) hit).getEntity();
				e.setFireTicks((int)(200*power));
				int damage = (int)Math.ceil(power*6);
				e.damage(new EntityDamageSource("yttr.rifle", user), damage);
			} else if (power > 0.5f && hit instanceof BlockHitResult) {
				BlockHitResult bhr = (BlockHitResult)hit;
				if (bhr.getType() == Type.MISS) return;
				if (user.world.isAir(bhr.getBlockPos()) || user.world.getBlockState(bhr.getBlockPos()).isIn(Yttr.FIRE_MODE_INSTABREAK)) {
					user.world.setBlockState(bhr.getBlockPos(), Blocks.FIRE.getDefaultState());
				} else {
					BlockPos bp2 = bhr.getBlockPos().offset(bhr.getSide());
					if (user.world.isAir(bp2)) {
						user.world.setBlockState(bp2, Blocks.FIRE.getDefaultState());
					}
				}
			}
			if (power > 1) {
				user.world.createExplosion(null, DamageSource.explosion(user), null, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, true, DestructionType.NONE);
				BlockPos base = new BlockPos(hit.getPos());
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						for (int z = -1; z <= 1; z++) {
							BlockPos bp = base.add(x, y, z);
							if (user.world.getBlockState(bp).isIn(Yttr.FIRE_MODE_INSTABREAK)) {
								user.world.setBlockState(bp, Blocks.FIRE.getDefaultState());
							}
						}
					}
				}
			}
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			user.setOnFireFor(20);
		}
	}
	;
	public final Formatting chatColor;
	public final int color;
	public final Supplier<ItemConvertible> item;
	public final int shotsPerItem;
	public final float speed;
	
	RifleMode(Formatting chatColor, int color, Supplier<ItemConvertible> item, int shotsPerItem, float speed) {
		this.chatColor = chatColor;
		this.color = color;
		this.item = item;
		this.shotsPerItem = shotsPerItem;
		this.speed = speed;
	}
	
	public boolean canFire(LivingEntity user, ItemStack stack, float power) {
		return power > 0;
	}
	
	public abstract void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit);
	public void handleBackfire(LivingEntity user, ItemStack stack) {}
	
}
