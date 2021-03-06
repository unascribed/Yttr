package com.unascribed.yttr;

import java.util.Locale;

import com.unascribed.yttr.mixin.AccessorEntity;

import com.google.common.base.Enums;
import com.google.common.base.Predicates;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
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
			int ammo = getRemainingAmmo(stack);
			RifleMode mode = getMode(stack);
			if (ammo <= 0) {
				if (user.abilities.creativeMode) {
					ammo = mode.shotsPerItem;
					
				} else {
					for (int i = 0; i < user.inventory.size(); i++) {
						ItemStack is = user.inventory.getStack(i);
						if (is.getItem() == mode.item.get().asItem()) {
							is.decrement(1);
							ammo = mode.shotsPerItem;
							user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_LOAD, user.getSoundCategory(), 3, 2f);
							user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_LOAD, user.getSoundCategory(), 3, 1.75f);
							user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_LOAD, user.getSoundCategory(), 3, 1.5f);
							break;
						}
					}
				}
			}
			if (ammo <= 0) {
				user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_FIRE_DUD, user.getSoundCategory(), 1, 1.25f);
				user.sendMessage(new TranslatableText("tip.yttr.rifle_no_ammo", mode.item.get().asItem().getName()), true);
				return TypedActionResult.fail(stack);
			}
			setRemainingAmmo(stack, ammo);
			world.playSoundFromEntity(null, user, Yttr.RIFLE_CHARGE, user.getSoundCategory(), 1, 1);
			user.setCurrentHand(hand);
			return TypedActionResult.success(stack, false);
		}
		return TypedActionResult.pass(stack);
	}
	
	public void attack(PlayerEntity user) {
		ItemStack stack = user.getMainHandStack();
		RifleMode[] val = RifleMode.values();
		RifleMode oldMode = getMode(stack);
		RifleMode mode = val[(oldMode.ordinal()+1)%val.length];
		if (setMode(stack, mode)) {
			user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_WASTE, user.getSoundCategory(), 3, 0.75f);
			user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_WASTE, user.getSoundCategory(), 3, 1f);
			user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_WASTE, user.getSoundCategory(), 3, 1.5f);
			if (user.world instanceof ServerWorld) {
				float r = NativeImage.getBlue(oldMode.color)/255f;
				float g = NativeImage.getGreen(oldMode.color)/255f;
				float b = NativeImage.getRed(oldMode.color)/255f;
				((ServerWorld)user.world).spawnParticles(new DustParticleEffect(r, g, b, 1), user.getPos().x, user.getPos().y+0.1, user.getPos().z, 12, 0.2, 0.1, 0.2, 1);
			}
		}
		user.setStackInHand(Hand.MAIN_HAND, stack);
		user.world.playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, Yttr.RIFLE_FIRE_DUD, user.getSoundCategory(), 1, 1.3f+(mode.ordinal()*0.1f));
		System.out.println("time,kw");
		for (int i = 0; i <= 140; i++) {
			System.out.println((i/20f)+","+(calculatePower(i)*500));
		}
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
		RifleMode mode = getMode(stack);
		int ammo = getRemainingAmmo(stack);
		if (useTicks > 30) {
			ammo--;
			setRemainingAmmo(stack, ammo);
		}
		if (!mode.canFire(user, stack, power)) {
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
				HitResult hr;
				if (ehr != null) {
					hr = new EntityHitResult(ehr.getEntity(), ehr.getEntity().getBoundingBox().expand(0.3).raycast(start, end).get());
				} else {
					hr = bhr;
				}
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeInt(user.getEntityId());
				int color = mode.color;
				if (power > 1.2) {
					color = 0xFFFFFFFF;
				} else {
					color |= (int)Math.min(255, power*255)<<24;
				}
				buf.writeInt(color);
				buf.writeFloat((float)hr.getPos().x);
				buf.writeFloat((float)hr.getPos().y);
				buf.writeFloat((float)hr.getPos().z);
				((ServerWorld)world).getChunkManager().sendToNearbyPlayers(user, ServerPlayNetworking.createS2CPacket(new Identifier("yttr", "beam"), buf));
				mode.handleFire(user, stack, power, hr);
			}
		}
	}
	
	public RifleMode getMode(ItemStack stack) {
		return Enums.getIfPresent(RifleMode.class, stack.hasTag() ? stack.getTag().getString("Mode") : RifleMode.DAMAGE.name()).or(RifleMode.DAMAGE);
	}
	
	public boolean setMode(ItemStack stack, RifleMode mode) {
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		RifleMode cur = getMode(stack);
		if (cur == mode) return false;
		stack.getTag().putString("Mode", mode.name());
		stack.getTag().putBoolean("WasSelected", false);
		int ammo = getRemainingAmmo(stack);
		setRemainingAmmo(stack, 0);
		return ammo > 0;
	}
	
	public int getRemainingAmmo(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt("RemainingAmmo") : 0;
	}
	
	public void setRemainingAmmo(ItemStack stack, int ammo) {
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		stack.getTag().putInt("RemainingAmmo", ammo);
	}
	
	private float calculatePower(int i) {
		// https://blob.jortage.com/blobs/d/b5f/db5fc6177921437d567ff378e36d8d9519998d1bc0de8ea2003d25258994c38ed8e69685a8fbd723466a8c0a27beb61786c1c78633e7d335584f399e78a8e212
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
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		setRemainingAmmo(stack, 0);
		if (!world.isClient) {
			getMode(stack).handleBackfire(user, stack);
		}
		if (user instanceof PlayerEntity) {
			((PlayerEntity) user).getItemCooldownManager().set(this, 160);
		}
		return stack;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		boolean wasSelected = stack.hasTag() && stack.getTag().getBoolean("WasSelected");
		if (selected != wasSelected) {
			if (!stack.hasTag()) stack.setTag(new CompoundTag());
			stack.getTag().putBoolean("WasSelected", selected);
			if (!wasSelected) {
				if (entity instanceof PlayerEntity && !world.isClient) {
					RifleMode mode = getMode(stack);
					((PlayerEntity)entity).sendMessage(new TranslatableText("tip.yttr.rifle_mode", new TranslatableText("yttr.rifle_mode."+mode.name().toLowerCase(Locale.ROOT)).formatted(Formatting.BOLD, mode.chatColor)), true);
				}
			} else {
				world.playSoundFromEntity(null, entity, Yttr.RIFLE_CHARGE_CANCEL, entity.getSoundCategory(), 1, 1);
			}
		}
		if (entity.age % 4 == 0 && entity instanceof PlayerEntity) {
			if (((PlayerEntity)entity).getItemCooldownManager().isCoolingDown(this)) {
				entity.playSound(Yttr.RIFLE_VENT, selected ? 0.5f : 0.2f, 0.6f+RANDOM.nextFloat()/3);
			}
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
