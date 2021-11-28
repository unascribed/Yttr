package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.AmmoCanItem;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class CanFillerBlockEntity extends BlockEntity implements SideyInventory, Tickable, Nameable, DelegatingInventory {

	private final SimpleInventory inv = new SimpleInventory(5);
	
	private int workTime;
	private int maxWorkTime;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return workTime;
				case 1: return maxWorkTime;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: workTime = value; break;
				case 1: maxWorkTime = value; break;
			}

		}

		@Override
		public int size() {
			return 2;
		}
	};
	
	public CanFillerBlockEntity() {
		super(YBlockEntities.CAN_FILLER);
		inv.addListener(i -> markDirty());
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public void tick() {
		if (!world.isClient) {
			ItemStack ammo = getStack(0);
			ItemStack propellant = getStack(1);
			ItemStack can = getStack(2);
			if (isRifleAmmo(ammo) && isPropellant(propellant) && isCan(can)) {
				RifleMode ammoType = getRifleAmmoType(ammo);
				maxWorkTime = 80;
				if (can.getItem() == YItems.AMMO_CAN && can.hasTag()) {
					if (!ammoType.name().equals(can.getTag().getString("Mode"))) {
						workTime = 0;
						return;
					}
					int shots = can.getTag().getInt("Shots");
					if (shots >= AmmoCanItem.CAPACITY) {
						workTime = 0;
						return;
					}
				}
				Item ammoRemainder = ammo.getItem().getRecipeRemainder();
				if (ammoRemainder != null && ammo.getCount() > 1) {
					workTime = 0;
					return;
				}
				Item propRemainder = propellant.getItem().getRecipeRemainder();
				if (!getStack(4).isEmpty() && getStack(4).getItem() != propRemainder) {
					workTime = 0;
					return;
				}
				if (!getStack(3).isEmpty()) {
					workTime = 0;
					return;
				}
				if (workTime < (can.getCount() == 1 ? maxWorkTime-20 : maxWorkTime)) {
					workTime++;
				} else {
					if (ammoRemainder != null) {
						setStack(0, new ItemStack(ammoRemainder));
					} else {
						ammo.decrement(1);
					}
					if (propRemainder != null) {
						ItemStack result2 = getStack(4);
						if (result2.isEmpty()) {
							result2 = new ItemStack(propRemainder);
						} else {
							result2.increment(1);
						}
						setStack(4, result2);
					}
					propellant.decrement(1);
					ItemStack result = can.split(1);
					if (result.getItem() != YItems.AMMO_CAN || !result.hasTag()) {
						result = new ItemStack(YItems.AMMO_CAN);
						result.setTag(new NbtCompound());
						result.getTag().putString("Mode", ammoType.name());
					}
					int shots = result.getTag().getInt("Shots");
					shots += (ammoType.shotsPerItem*3)/2;
					if (shots > AmmoCanItem.CAPACITY) {
						shots = AmmoCanItem.CAPACITY;
					}
					result.getTag().putInt("Shots", shots);
					if (can.isEmpty() && !ammo.isEmpty() && !propellant.isEmpty() && shots < AmmoCanItem.CAPACITY) {
						setStack(2, result);
					} else {
						setStack(3, result);
					}
					workTime = 0;
				}
			} else {
				workTime = 0;
			}
		}
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag = super.writeNbt(tag);
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("WorkTime", workTime);
		tag.putInt("MaxWorkTime", maxWorkTime);
		return tag;
	}
	
	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		workTime = tag.getInt("WorkTime");
		maxWorkTime = tag.getInt("MaxWorkTime");
	}
	
	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 8*8;
	}
	
	@Override
	public Text getName() {
		return new TranslatableText("block.yttr.can_filler");
	}
	
	@Override
	public boolean canAccess(int slot, Direction side) {
		if (side == Direction.UP) return slot == 2;
		if (side == Direction.DOWN) return slot == 3 || slot == 4 || (slot == 0 && !isRifleAmmo(getStack(0)));
		return slot == 0 || slot == 1;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.UP) return slot == 2 && isCan(stack);
		if (dir == Direction.DOWN) return false;
		if (slot == 1) return isPropellant(stack);
		if (slot == 0) return isRifleAmmo(stack);
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN) return slot == 3 || slot == 4 || (slot == 0 && !isRifleAmmo(getStack(0)));
		return false;
	}
	
	public static boolean isCan(ItemStack stack) {
		if (stack.getItem() == YItems.EMPTY_AMMO_CAN) return true;
		return stack.getItem() == YItems.AMMO_CAN && (!stack.hasTag() || stack.getTag().getInt("Shots") < AmmoCanItem.CAPACITY);
	}
	
	public static boolean isPropellant(ItemStack stack) {
		return stack.getItem() == YItems.GLOWING_GAS;
	}
	
	public static RifleMode getRifleAmmoType(ItemStack stack) {
		for (RifleMode mode : RifleMode.VALUES) {
			if (mode.item.get().asItem() == stack.getItem()) {
				return mode;
			}
		}
		return null;
	}
	
	public static boolean isRifleAmmo(ItemStack stack) {
		return getRifleAmmoType(stack) != null;
	}

}
