package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AmmoPackScreenHandler extends ScreenHandler {

	private final Inventory ammoPack;
	
	private static class APSlot extends Slot {

		public APSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}
		
		@Override
		public boolean canInsert(ItemStack stack) {
			return stack.getItem() == YItems.AMMO_CAN;
		}
		
	}
	
	public AmmoPackScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(6), syncId, playerInv);
	}
	
	public AmmoPackScreenHandler(Inventory ammoPack, int syncId, PlayerInventory playerInv) {
		super(YHandledScreens.AMMO_PACK, syncId);
		
		this.ammoPack = ammoPack;
		
		addSlot(new APSlot(ammoPack, 0, 62, 18));
		addSlot(new APSlot(ammoPack, 1, 98, 18));
		addSlot(new APSlot(ammoPack, 2, 62, 36));
		addSlot(new APSlot(ammoPack, 3, 98, 36));
		addSlot(new APSlot(ammoPack, 4, 62, 54));
		addSlot(new APSlot(ammoPack, 5, 98, 54));
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 81 + y * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInv, i, 8 + i * 18, 139));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return ammoPack.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack out = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			out = slotStack.copy();
			if (index >= 0 && index <= 5) {
				if (!insertItem(slotStack, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(slotStack, out);
			} else if (index > 6) {
				if (slotStack.getItem() == YItems.AMMO_CAN) {
					if (!insertItem(slotStack, 0, 6, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!insertItem(slotStack, 4, 40, false)) {
				// move anything else to player inventory
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (slotStack.getCount() == out.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, slotStack);
		}

		return out;
	}
	
}
