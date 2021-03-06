package com.unascribed.yttr;

import com.unascribed.yttr.mixin.AccessorEntity;

import com.google.common.base.Predicates;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class RifleItem extends Item {

	public RifleItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (hand == Hand.MAIN_HAND) {
			world.playSoundFromEntity(null, user, Yttr.RIFLE_CHARGE, user.getSoundCategory(), 1, 1);
			user.setCurrentHand(hand);
			return TypedActionResult.success(stack, false);
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 140;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		super.onStoppedUsing(stack, world, user, remainingUseTicks);
		world.playSoundFromEntity(null, user, Yttr.RIFLE_CHARGE_CANCEL, user.getSoundCategory(), 1, 1);
		int useTicks = getMaxUseTime(stack)-remainingUseTicks;
		float power = calculatePower(useTicks);
		if (power <= 0.1) {
			user.playSound(Yttr.RIFLE_FIRE_DUD, 1, 1);
		} else {
			if (power > 1.1) {
				if (power > 1.2) {
					if (power >= 1.29) {
						user.playSound(Yttr.RIFLE_FIRE, 2, 0.5f);
						user.playSound(Yttr.RIFLE_FIRE, 2, 0.5f);
						user.playSound(Yttr.RIFLE_FIRE, 2, 2f);
						user.playSound(Yttr.RIFLE_FIRE, 2, 1f);
					}
					user.playSound(Yttr.RIFLE_FIRE, 2, 0.75f);
					user.playSound(Yttr.RIFLE_FIRE, 2, 0.65f);
				}
				user.playSound(Yttr.RIFLE_FIRE, 2, 0.5f);
				user.playSound(Yttr.RIFLE_FIRE, 1, 1);
				user.playSound(Yttr.RIFLE_FIRE, 1, 2);
				user.playSound(Yttr.RIFLE_FIRE, 1, 1.25f);
				if (user instanceof PlayerEntity) {
					((PlayerEntity) user).getItemCooldownManager().set(this, 30);
				}
			} else {
				user.playSound(Yttr.RIFLE_FIRE, 1, 0.9f+(power/4));
				if (power > 1) {
					user.playSound(Yttr.RIFLE_FIRE, 1, 0.75f);
				}
			}
			if (world instanceof ServerWorld) {
				Vec3d start = getMuzzlePos(user, false);
				Vec3d end = user.getCameraPosVec(0).add(user.getRotationVec(0).multiply(256));
				BlockHitResult bhr = world.raycast(new RaycastContext(start, end, ShapeType.COLLIDER, FluidHandling.NONE, user));
				EntityHitResult ehr = ProjectileUtil.getEntityCollision(user.world, user, start, bhr.getPos(), new Box(start, end), Predicates.alwaysTrue());
				Vec3d strike;
				HitResult hr;
				if (ehr != null) {
					hr = ehr;
					strike = ehr.getEntity().getBoundingBox().expand(0.3).raycast(start, end).get();
				} else {
					hr = bhr;
					strike = bhr.getPos();
				}
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeInt(user.getEntityId());
				int color = 0xFFFF00;
				if (power > 1.2) {
					color = 0xFFFFFFFF;
				} else if (power > 1.1) {
					color = 0xFFFFFFAA;
				} else if (power > 1) {
					color = 0xFFFFFF88;
				} else {
					color |= (int)(power*255)<<24;
				}
				buf.writeInt(color);
				buf.writeFloat((float)strike.x);
				buf.writeFloat((float)strike.y);
				buf.writeFloat((float)strike.z);
				((ServerWorld)world).getChunkManager().sendToNearbyPlayers(user, ServerPlayNetworking.createS2CPacket(new Identifier("yttr", "beam"), buf));
				if (user instanceof PlayerEntity) {
					((PlayerEntity)user).sendMessage(new LiteralText(((int)(power*500))+"kW"), true);
				}
			}
		}
	}
	
	private float calculatePower(int i) {
		// https://blob.jortage.com/blobs/b/e97/be9729a693b8600113d773e839c6ca234a2113f3fb846d9c88915d8df8c4104fcb62a3be35abf8cba1dd2950e66ca48597cc7384755f990e8324ce26f0dd1459
		float power = 0;
		if (i == 132 || i == 133) {
			power = 1.3f;
		} else if (i > 30) {
			int j = i - 30;
			if (j > 80) {
				power = 0.8f+(MathHelper.sin(((j-90)/25f)*(float)Math.PI)/2);
			} else if (j > 60) {
				power = 0.7f-(MathHelper.sin(((j-60)/40f)*(float)Math.PI)*0.4f);
			} else {
				power = MathHelper.sin((j/80f)*(float)Math.PI);
			}
		}
		return power;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		user.playSound(Yttr.RIFLE_OVERCHARGE, 1, 1);
		user.damage(new DamageSource("yttr.rifle_overcharge") {}, 8);
		user.setOnFireFor(3);
		if (user instanceof PlayerEntity) {
			((PlayerEntity) user).getItemCooldownManager().set(this, 160);
		}
		return stack;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		int useTicks = getMaxUseTime(stack)-remainingUseTicks;
		if (user instanceof PlayerEntity) {
//			((PlayerEntity)user).sendMessage(new LiteralText(((int)(calculatePower(useTicks)*500))+"kW"), true);
		}
	}
	
	public static Vec3d getMuzzlePos(Entity entity, boolean firstPerson) {
		Arm arm = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainArm() : Arm.RIGHT;
		Vec3d eyes = entity.getCameraPosVec(0);
		Vec3d look = entity.getRotationVec(0);
		Vec3d right = ((AccessorEntity)entity).yttr$callGetRotationVector(0, entity.getYaw(0)+90);
		Vec3d down = look.crossProduct(right);
		if (arm == Arm.LEFT) right = right.multiply(-1);
		if (firstPerson) {
			return eyes.add(look.multiply(0.06)).add(right.multiply(0.0325)).add(down.multiply(0.0125));
		} else {
			return eyes.add(look.multiply(0.7)).add(right.multiply(0.25)).add(down.multiply(0.0125));
		}
	}
	
}
