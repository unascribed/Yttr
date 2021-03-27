package com.unascribed.yttr;

import java.util.List;

import com.google.common.primitives.Ints;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SnareItem extends Item {

	public SnareItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof PlayerEntity) return ActionResult.FAIL;
		if (!stack.hasTag() || !stack.getTag().contains("Contents")) {
			if (entity.getType().isIn(Yttr.UNSNAREABLE_TAG)) return ActionResult.FAIL;
			if (user.world.isClient) return ActionResult.SUCCESS;
			entity.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f, 0.5f);
			entity.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f, 0.75f);
			entity.playSound(SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.2f, 2f);
			CompoundTag data = new CompoundTag();
			if (entity.saveSelfToTag(data)) {
				entity.remove();
				if (!stack.hasTag()) stack.setTag(new CompoundTag());
				stack.getTag().put("Contents", data);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAIL;
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getStack().hasTag() && context.getStack().getTag().contains("Contents")) {
			if (context.getWorld().isClient) return ActionResult.SUCCESS;
			context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 0.75f);
			context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0f, 0.95f);
			context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.PLAYERS, 0.3f, 1.75f);
			release(context.getWorld(), context.getStack(), context.getHitPos(), -context.getPlayerYaw());
		}
		return ActionResult.PASS;
	}
	
	@Override
	public Text getName(ItemStack stack) {
		EntityType<?> type = getEntityType(stack);
		if (type != null) {
			return new TranslatableText("item.yttr.snare.filled", type.getName());
		}
		return super.getName(stack);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		Text msg = getContainmentMessage(world, stack);
		if (msg != null) {
			tooltip.add(msg);
		}
	}
	
	private Text getContainmentMessage(World world, ItemStack stack) {
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			int ticksLeft = ((stack.getMaxDamage()-stack.getDamage())/dmg)*(EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack)+1);
			ticksLeft -= getCheatedTicks(world, stack);
			if (ticksLeft < 0) {
				return new TranslatableText("tip.yttr.snare.failed").formatted(Formatting.RED);
			} else {
				int seconds = ticksLeft/20;
				int minutes = seconds/60;
				seconds = seconds%60;
				return new TranslatableText("tip.yttr.snare.unstable", minutes, Integer.toString(seconds+100).substring(1))
						.formatted(minutes <= 1 ? minutes == 0 && seconds <= 30 ? Formatting.RED : Formatting.YELLOW : Formatting.GRAY);
			}
		} else if (stack.hasTag() && stack.getTag().contains("Contents")) {
			return new TranslatableText("tip.yttr.snare.stable").formatted(Formatting.GRAY);
		} else {
			return null;
		}
	}

	private int getCheatedTicks(World world, ItemStack stack) {
		if (world.isClient) return 0;
		long lastUpdate = stack.hasTag() ? stack.getTag().getLong("LastUpdate") : 0;
		if (lastUpdate == 0) return 0;
		long cheatedTicks = world.getServer().getOverworld().getTime()-lastUpdate;
		if (cheatedTicks < 10) return 0;
		return Ints.saturatedCast(cheatedTicks);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world.isClient) return;
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			if (stack.damage(dmg*(getCheatedTicks(world, stack)+1), RANDOM, null)) {
				stack.decrement(1);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, SoundEvents.ENTITY_ITEM_PICKUP, entity.getSoundCategory(), 1.0f, 0.75f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, SoundEvents.ENTITY_ITEM_PICKUP, entity.getSoundCategory(), 1.0f, 0.95f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, entity.getSoundCategory(), 0.7f, 1.75f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, entity.getSoundCategory(), 0.5f, 1.3f);
				release(world, stack, entity.getPos(), entity.getYaw(1));
			} else {
				stack.getTag().putLong("LastUpdate", world.getServer().getOverworld().getTime());
			}
		}
		if (entity instanceof PlayerEntity && selected) {
			Text msg = getContainmentMessage(world, stack);
			if (msg != null) {
				((PlayerEntity) entity).sendMessage(msg, true);
			}
		}
	}
	
	public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			if (stack.damage(dmg*(getCheatedTicks(world, stack)+1), RANDOM, null)) {
				stack.decrement(1);
				world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 0.75f);
				world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 0.95f);
				world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.7f, 1.75f);
				world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1.3f);
				release(world, stack, Vec3d.ofBottomCenter(pos.up()), 0);
			} else {
				stack.getTag().putLong("LastUpdate", world.getServer().getOverworld().getTime());
			}
		}
	}
	
	private int calculateDamageRate(World world, ItemStack stack) {
		if (stack.hasTag() && stack.getTag().getBoolean("Unbreakable")) return 0;
		EntityType<?> type = getEntityType(stack);
		if (type != null) {
			CompoundTag data = stack.getTag().getCompound("Contents");
			int dmg = MathHelper.ceil(data.getFloat("Health")*MathHelper.sqrt(type.getDimensions().height*type.getDimensions().width));
			switch (type.getSpawnGroup()) {
				case AMBIENT:
				case WATER_AMBIENT:
					dmg /= 6;
					break;
				case CREATURE:
				case WATER_CREATURE:
					dmg /= 4;
					break;
				default:
					break;
			}
			if (type.isIn(Yttr.BOSSES_TAG)) {
				dmg *= 4;
			}
			return dmg;
		}
		return 0;
	}

	public EntityType<?> getEntityType(ItemStack stack) {
		CompoundTag data = stack.getTag().getCompound("Contents");
		Identifier id = Identifier.tryParse(data.getString("id"));
		if (id == null) return null;
		return Registry.ENTITY_TYPE.getOrEmpty(id).orElse(null);
	}

	private void release(World world, ItemStack stack, Vec3d pos, float yaw) {
		EntityType<?> type = getEntityType(stack);
		if (type != null) {
			Entity e = type.create(world);
			e.fromTag(stack.getTag().getCompound("Contents"));
			e.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, 0);
			world.spawnEntity(e);
			stack.getTag().remove("Contents");
		}
	}
	
}
