package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.init.YBlockEntities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class CentrifugeBlockEntity extends BlockEntity implements SidedInventory, Tickable, Nameable {

	private final SimpleInventory inv = new SimpleInventory(6);
	
	private int burnTime;
	private int fuelTime;
	private int spinTime;
	private int spinTimeTotal;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return burnTime;
				case 1: return fuelTime;
				case 2: return spinTime;
				case 3: return spinTimeTotal;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: burnTime = value; break;
				case 1: fuelTime = value; break;
				case 2: spinTime = value; break;
				case 3: spinTimeTotal = value; break;
			}

		}

		@Override
		public int size() {
			return 4;
		}
	};
	
	public CentrifugeBlockEntity() {
		super(YBlockEntities.CENTRIFUGE);
		
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public void tick() {
		
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
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 8*8;
	}
	
	@Override
	public Text getName() {
		return new TranslatableText("block.yttr.centrifuge");
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return new int[] {0};
		}
		if (side == Direction.DOWN) {
			return new int[] {1, 2, 3, 4};
		}
		return new int[] {5};
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.UP) return slot == 0;
		if (dir == Direction.DOWN) return false;
		return slot == 5 && FurnaceBlockEntity.canUseAsFuel(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN) return slot != 0 && slot != 5;
		return false;
	}

}
