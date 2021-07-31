package com.unascribed.yttr.item;

import java.util.concurrent.TimeUnit;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.TicksAlwaysItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class SpectralAxeItem extends AxeItem implements TicksAlwaysItem, ItemColorProvider {

	public SpectralAxeItem() {
		super(new ToolMaterial() {
			
			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.EMPTY;
			}
			
			@Override
			public float getMiningSpeedMultiplier() {
				return 16;
			}
			
			@Override
			public int getMiningLevel() {
				return 0;
			}
			
			@Override
			public int getEnchantability() {
				return 0;
			}
			
			@Override
			public int getDurability() {
				return 64;
			}
			
			@Override
			public float getAttackDamage() {
				return 0;
			}
		}, 0, 0, new Item.Settings()
				.rarity(Rarity.UNCOMMON)
				.group(YItemGroups.MAIN)
				.maxDamage(64));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world.isClient) return;
		if (!(entity instanceof PlayerEntity) && entity.age > 0) {
			playGlobalVanishEffect(entity.world, entity.getPos().x, entity.getBoundingBox().getCenter().y, entity.getPos().z);
			stack.setCount(0);
			return;
		}
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}
		if (!stack.getTag().contains("CreatedAt")) {
			stack.getTag().putLong("CreatedAt", System.currentTimeMillis());
		}
		if (System.currentTimeMillis()-stack.getTag().getLong("CreatedAt") > TimeUnit.HOURS.toMillis(6)) {
			playVanishEffect(entity);
			stack.setCount(0);
		}
	}
	
	@Override
	public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
		stack.setCount(0);
		playGlobalVanishEffect(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
	}
	
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return state.isIn(BlockTags.LOGS) ? 16 : state.isIn(BlockTags.LEAVES) ? 64 : 1;
	}
	
	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return true;
	}
	
	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.isIn(BlockTags.LOGS)) {
			if (!world.isClient && state.getHardness(world, pos) != 0) {
				stack.damage(1, miner, (e) -> {
					playVanishEffect(e);
				});
			}
			return true;
		}
		return false;
	}
	
	private void playVanishEffect(Entity e) {
		if (e instanceof ServerPlayerEntity) {
			// the spectral axe does not actually exist, so only the person who broke it can hear/see it vanish
			ServerPlayerEntity spe = (ServerPlayerEntity)e;
			PlaySoundFromEntityS2CPacket spkt = new PlaySoundFromEntityS2CPacket(YSounds.SPECTRAL_AXE_DISAPPEAR, SoundCategory.PLAYERS, e, 1f, 1);
			Vec3d hand = RifleItem.getMuzzlePos(e, true);
			ParticleS2CPacket ppkt = new ParticleS2CPacket(new DustParticleEffect(0.95f, 0.95f, 1, 0.6f), false, hand.x, hand.y, hand.z, 0.25f, 0.125f, 0.25f, 0, 20);
			spe.networkHandler.sendPacket(spkt);
			spe.networkHandler.sendPacket(ppkt);
		}
	}
	
	private void playGlobalVanishEffect(World w, double x, double y, double z) {
		w.playSound(null, x, y, z, YSounds.SPECTRAL_AXE_DISAPPEAR, SoundCategory.PLAYERS, 1, 1);
		if (w instanceof ServerWorld) {
			((ServerWorld)w).spawnParticles(new DustParticleEffect(0.95f, 0.95f, 1, 0.6f), x, y, z, 20, 0.5f, 0.5f, 0.5f, 0);
		}
	}

	private static final long epoch = System.currentTimeMillis();
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		long t = System.currentTimeMillis()-epoch;
		float range = 64;
		int a = (int)(((MathHelper.sin(t/500f)+1)*(range/2))+(255-range));
		return a<<16|a<<8|a;
	}

}
