package com.unascribed.yttr.inventory;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.unascribed.yttr.init.YHandledScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class DSUScreenHandler extends GenericContainerScreenHandler {

	public static final ThreadLocal<MutableInt> increaseStackSize = ThreadLocal.withInitial(() -> new MutableInt(0));
	
	public DSUScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new HighStackSimpleInventory(9*5));
	}
	
	public DSUScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(YHandledScreens.DSU, syncId, playerInventory, inventory, 5);
	}

	@Override
	public void sendContentUpdates() {
		try {
			increaseStackSize.get().increment();
			super.sendContentUpdates();
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		try {
			increaseStackSize.get().increment();
			return super.onButtonClick(player, id);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int i) {
		try {
			increaseStackSize.get().increment();
			return super.transferSlot(player, i);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
		if (i >= 0 && i < slots.size()) {
			Slot slot = slots.get(i);
			if (slot != null && !(slot.inventory instanceof PlayerInventory) && actionType == SlotActionType.QUICK_MOVE) {
				ItemStack all = slot.getStack().copy();
				ItemStack some = slot.getStack().copy();
				some.setCount(Math.min(all.getCount(), all.getMaxCount()));
				slot.setStack(some);
				if (!insertItem(some, slots.size()-9, slots.size(), false)) {
					insertItem(some, 5*9, slots.size()-9, false);
				}
				all.setCount(all.getCount()-(all.getMaxCount()-some.getCount()));
				slot.setStack(all);
				return ItemStack.EMPTY;
			}
		}
		try {
			increaseStackSize.get().increment();
			return super.onSlotClick(i, j, actionType, playerEntity);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		if (slot.inventory instanceof PlayerInventory) return super.canInsertIntoSlot(stack, slot);
		try {
			increaseStackSize.get().increment();
			return super.canInsertIntoSlot(stack, slot);
		} finally {
			increaseStackSize.get().decrement();
		}
	}
	
	@Override
	public void onContentChanged(Inventory inventory) {
		try {
			increaseStackSize.get().increment();
			super.onContentChanged(inventory);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		try {
			increaseStackSize.get().increment();
			super.setStackInSlot(slot, stack);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public void updateSlotStacks(List<ItemStack> stacks) {
		try {
			increaseStackSize.get().increment();
			super.updateSlotStacks(stacks);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

	@Override
	public boolean canInsertIntoSlot(Slot slot) {
		if (slot.inventory instanceof PlayerInventory) return super.canInsertIntoSlot(slot);
		try {
			increaseStackSize.get().increment();
			return super.canInsertIntoSlot(slot);
		} finally {
			increaseStackSize.get().decrement();
		}
	}

}
