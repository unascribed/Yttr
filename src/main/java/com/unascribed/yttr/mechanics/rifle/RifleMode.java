package com.unascribed.yttr.mechanics.rifle;

import java.util.function.Supplier;

import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mechanics.VoidLogic;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion.DestructionType;

public enum RifleMode {
	DAMAGE(Formatting.RED, 0xFF0000, () -> Items.REDSTONE, 12, 2) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (hit instanceof EntityHitResult) {
				int damage = (int)Math.ceil(power*14);
				((EntityHitResult) hit).getEntity().damage(new EntityDamageSource("yttr.rifle", user), damage);
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
			user.world.createExplosion(null, DamageSource.explosion(user), null, user.getPos().x, user.getPos().y, user.getPos().z, 5.5f, false, DestructionType.DESTROY);
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
				if (user.world.isAir(bhr.getBlockPos()) || user.world.getBlockState(bhr.getBlockPos()).isIn(YTags.Block.FIRE_MODE_INSTABREAK)) {
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
							if (user.world.getBlockState(bp).isIn(YTags.Block.FIRE_MODE_INSTABREAK)) {
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
	},
	VOID(Formatting.BLACK, 0x000000, () -> YItems.VOID_BUCKET, 1, 0.75f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (!(user instanceof PlayerEntity)) return;
			VoidLogic.doVoid((PlayerEntity)user, user.world, hit.getPos(), Math.round(7.5f*power)+1);
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			if (!(user instanceof PlayerEntity)) return;
			VoidLogic.doVoid((PlayerEntity)user, user.world, user.getPos(), 12);
		}
		
	},
	LIGHT(Formatting.YELLOW, 0xFFFF00, () -> YItems.GLOWING_GAS, 8, 2f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			Vec3d start = RifleItem.getMuzzlePos(user, false);
			double len = MathHelper.sqrt(start.squaredDistanceTo(hit.getPos()));
			double diffX = hit.getPos().x-start.x;
			double diffY = hit.getPos().y-start.y;
			double diffZ = hit.getPos().z-start.z;
			BlockPos.Mutable mut = new BlockPos.Mutable();
			int count = (int)(len*4);
			for (int i = 0; i < count; i++) {
				double t = (i/(double)count);
				double x = start.x+(diffX*t);
				double y = start.y+(diffY*t);
				double z = start.z+(diffZ*t);
				mut.set(x, y, z);
				if (user.world.getBlockState(mut).isAir()) {
					user.world.setBlockState(mut, (power > 1.1f ? YBlocks.PERMANENT_LIGHT_AIR : YBlocks.TEMPORARY_LIGHT_AIR).getDefaultState());
				}
			}
			if (hit instanceof EntityHitResult) {
				Entity e = ((EntityHitResult)hit).getEntity();
				if (e instanceof LivingEntity) {
					((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, (int)(200*power)));
				}
			} else if (hit instanceof BlockHitResult && power > 0.8f) {
				BlockHitResult bhr = (BlockHitResult)hit;
				BlockPos end = bhr.getBlockPos().offset(bhr.getSide());
				if (user.world.getBlockState(bhr.getBlockPos()).isAir()) {
					user.world.setBlockState(bhr.getBlockPos(), YBlocks.PERMANENT_LIGHT_AIR.getDefaultState());
				} else if (user.world.getBlockState(end).isAir()) {
					user.world.setBlockState(end, YBlocks.PERMANENT_LIGHT_AIR.getDefaultState());
				}
			}
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300));
			for (BlockPos bp : BlockPos.iterate(user.getBlockPos().add(-2, -2, -2), user.getBlockPos().add(2, 2, 2))) {
				if (user.world.getBlockState(bp).isAir()) {
					user.world.setBlockState(bp, YBlocks.TEMPORARY_LIGHT_AIR.getDefaultState());
				}
			}
		}
	}
	;
	public static final ImmutableList<RifleMode> VALUES = ImmutableList.copyOf(values());
	
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
	
	public RifleMode next() {
		return VALUES.get((ordinal()+1)%VALUES.size());
	}
	
	public RifleMode prev() {
		if (ordinal() == 0) return VALUES.get(VALUES.size()-1);
		return VALUES.get(ordinal()-1);
	}
	
}
