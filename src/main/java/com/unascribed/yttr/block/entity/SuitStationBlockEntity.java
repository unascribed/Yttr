package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.SuitResource;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.item.SuitArmorItem;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class SuitStationBlockEntity extends BlockEntity implements Tickable, SidedInventory {

	public int lastCollisionTick;
	
	private final SimpleInventory inv = new SimpleInventory(8);
	
	private int fuelTime;
	private int maxFuelTime;
	private int fluxLeft;
	private int maxFluxLeft;
	
	private int meltedGlowstoneLeft;
	private int armorPlatingLeft;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return fuelTime;
				case 1: return maxFuelTime;
				case 2: return fluxLeft;
				case 3: return maxFluxLeft;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: fuelTime = value; break;
				case 1: maxFuelTime = value; break;
				case 2: fluxLeft = value; break;
				case 3: maxFluxLeft = value; break;
			}

		}

		@Override
		public int size() {
			return 4;
		}
	};
	
	public SuitStationBlockEntity() {
		super(YBlockEntities.SUIT_STATION);
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}

	@Override
	public void tick() {
		if (fuelTime > 0) {
			if (world.random.nextInt(140) == 0) world.playSound(null, pos, YSounds.SUIT_STATION_CRACKLE, SoundCategory.BLOCKS, 1, 1);
			fuelTime--;
		}
		boolean entireSuitPresent = true;
		for (int i = 0; i < 4; i++) {
			if (!(getStack(i).getItem() instanceof SuitArmorItem)) {
				entireSuitPresent = false;
				break;
			}
		}
		if (entireSuitPresent) {
			ItemStack chest = getStack(1);
			SuitArmorItem sai = (SuitArmorItem)chest.getItem();
			sai.replenishResource(chest, SuitResource.OXYGEN, 100);
			if (sai.getResourceAmount(chest, SuitResource.FUEL) < SuitResource.FUEL.getMaximum() && (meltedGlowstoneLeft > 0 || getStack(4).getItem() == Items.GLOWSTONE_DUST)) {
				if (fuelTime <= 0 && FurnaceBlockEntity.canUseAsFuel(getStack(5))) {
					ItemStack fuelStack = removeStack(5, 1);
					fuelTime = FurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
					maxFuelTime = fuelTime;
				}
				if (fuelTime > 0) {
					if (meltedGlowstoneLeft <= 0) {
						if (getStack(4).getItem() == Items.GLOWSTONE_DUST) {
							removeStack(4, 1);
							world.playSound(null, pos, YSounds.SUIT_STATION_MELT, SoundCategory.BLOCKS, 1, 2);
							meltedGlowstoneLeft = 500;
						}
					} else {
						int amt = Math.min(SuitResource.FUEL.getMaximum()-sai.getResourceAmount(chest, SuitResource.FUEL), Math.min(30, meltedGlowstoneLeft));
						meltedGlowstoneLeft -= amt;
						sai.replenishResource(chest, SuitResource.FUEL, meltedGlowstoneLeft);
					}
				}
			}
			if (sai.getResourceAmount(chest, SuitResource.INTEGRITY) < SuitResource.INTEGRITY.getMaximum()) {
				int pct = (sai.getResourceAmount(chest, SuitResource.INTEGRITY)*100)/SuitResource.INTEGRITY.getMaximum();
				if (fluxLeft <= 0 && getStack(7).getItem().isIn(YTags.Item.FLUXES)) {
					removeStack(7, 1);
					fluxLeft = 100;
					maxFluxLeft = fluxLeft;
				}
				if (fluxLeft > 0) {
					boolean canRepair = true;
					if (pct < 75) {
						if (armorPlatingLeft <= 0) {
							if (getStack(6).getItem() == YItems.ARMOR_PLATING) {
								removeStack(6, 1);
								world.playSound(null, pos, YSounds.SUIT_STATION_USE_PLATE, SoundCategory.BLOCKS, 1, 1);
								armorPlatingLeft = 305;
							}
						}
						if (armorPlatingLeft > 0) {
							armorPlatingLeft--;
						} else {
							canRepair = false;
						}
					}
					if (canRepair) {
						if (world.random.nextInt(120) == 0) world.playSound(null, pos, YSounds.SUIT_STATION_WELD, SoundCategory.BLOCKS, 0.3f, 0.8f+((world.random.nextFloat()*0.4f)));
						fluxLeft--;
						sai.replenishResource(chest, SuitResource.INTEGRITY, 50);
					}
				}
			}
		}
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("FuelTime", fuelTime);
		tag.putInt("MaxFuelTime", maxFuelTime);
		tag.putInt("FluxLeft", fluxLeft);
		tag.putInt("MaxFluxLeft", maxFluxLeft);
		tag.putInt("MeltedGlowstoneLeft", meltedGlowstoneLeft);
		tag.putInt("ArmorPlatingLeft", armorPlatingLeft);
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		fuelTime = tag.getInt("FuelTime");
		maxFuelTime = tag.getInt("MaxFuelTime");
		fluxLeft = tag.getInt("FluxLeft");
		maxFluxLeft = tag.getInt("MaxFluxLeft");
		meltedGlowstoneLeft = tag.getInt("MeltedGlowstoneLeft");
		armorPlatingLeft = tag.getInt("ArmorPlatingLeft");
	}

	@Override
	public int size() {
		return inv.size();
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return inv.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return inv.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return inv.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv.setStack(slot, stack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.getSquaredDistance(player.getPos(), false) < 5*5;
	}

	@Override
	public void clear() {
		inv.clear();
	}
	
	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (slot == 0) return isSuit(stack, EquipmentSlot.HEAD);
		if (slot == 1) return isSuit(stack, EquipmentSlot.CHEST);
		if (slot == 2) return isSuit(stack, EquipmentSlot.LEGS);
		if (slot == 3) return isSuit(stack, EquipmentSlot.FEET);
		if (slot == 4) return stack.getItem() == Items.GLOWSTONE_DUST;
		if (slot == 5) return FurnaceBlockEntity.canUseAsFuel(stack);
		if (slot == 6) return stack.getItem() == YItems.ARMOR_PLATING;
		if (slot == 7) return stack.getItem().isIn(YTags.Item.FLUXES);
		return false;
	}
	
	public static boolean isSuit(ItemStack stack, EquipmentSlot slot) {
		return stack.getItem() instanceof SuitArmorItem && ((SuitArmorItem)stack.getItem()).getSlotType() == slot;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	}

}
