package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YScreenTypes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class VoidFilterScreenHandler extends ScreenHandler {

	private final World world;
	
	private final Inventory voidFilter;
	private final PropertyDelegate properties;
	
	private static class OutputSlot extends Slot {

		public OutputSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}
		
		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}
		
	}
	
	public VoidFilterScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(9), syncId, playerInv, new ArrayPropertyDelegate(2));
	}
	
	public VoidFilterScreenHandler(Inventory voidFilter, int syncId, PlayerInventory playerInv, PropertyDelegate properties) {
		super(YScreenTypes.VOID_FILTER, syncId);
		world = playerInv.player.world;
		
		this.voidFilter = voidFilter;
		this.properties = properties;
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new OutputSlot(voidFilter, x+(y*3), 116+(x*18), 17+(y*18)));
			}
		}
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}
		
		addProperties(properties);
		
		voidFilter.onOpen(playerInv.player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return voidFilter.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack there = slot.getStack();
			result = there.copy();
			if (index < 9) {
				if (!insertItem(there, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!insertItem(there, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (there.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (there.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, there);
		}

		return result;
	}
	
	public int getProgress() {
		return properties.get(0);
	}
	
	public int getMaxProgress() {
		return properties.get(1);
	}

}
