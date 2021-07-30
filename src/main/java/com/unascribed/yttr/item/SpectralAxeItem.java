package com.unascribed.yttr.item;

import java.util.concurrent.TimeUnit;

import com.unascribed.yttr.init.YItemGroups;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
		if (!(entity instanceof PlayerEntity)) {
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
			stack.setCount(0);
		}
	}
	
	@Override
	public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
		stack.setCount(0);
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
			return super.postMine(stack, world, state, pos, miner);
		}
		return false;
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
