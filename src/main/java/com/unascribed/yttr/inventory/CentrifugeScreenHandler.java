package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YScreenTypes;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CentrifugeScreenHandler extends ScreenHandler {

	private final Inventory centrifuge;
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
	
	public CentrifugeScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(6), syncId, playerInv, new ArrayPropertyDelegate(4));
	}
	
	public CentrifugeScreenHandler(Inventory centrifuge, int syncId, PlayerInventory playerInv, PropertyDelegate properties) {
		super(YScreenTypes.CENTRIFUGE, syncId);
		this.centrifuge = centrifuge;
		this.properties = properties;
		
		addSlot(new Slot(centrifuge, 0, 80, 48));
		addSlot(new OutputSlot(centrifuge, 1, 84, 13));
		addSlot(new OutputSlot(centrifuge, 2, 114, 52));
		addSlot(new OutputSlot(centrifuge, 3, 76, 82));
		addSlot(new OutputSlot(centrifuge, 4, 46, 44));
		addSlot(new Slot(centrifuge, 5, 8, 86) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return FurnaceBlockEntity.canUseAsFuel(stack);
			}
		});
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 119 + y * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInv, i, 8 + i * 18, 177));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return centrifuge.canPlayerUse(player);
	}

}
