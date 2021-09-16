package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.init.YHandledScreens;

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
import net.minecraft.world.World;

public class CentrifugeScreenHandler extends ScreenHandler {

	private final World world;
	
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
		super(YHandledScreens.CENTRIFUGE, syncId);
		world = playerInv.player.world;
		
		this.centrifuge = centrifuge;
		this.properties = properties;
		
		addSlot(new Slot(centrifuge, 0, 80, 48));
		int ofs = playerInv.player.getHorizontalFacing().getHorizontal();
		addSlot(new OutputSlot(centrifuge, ((0+ofs)%4)+1, 84, 13));
		addSlot(new OutputSlot(centrifuge, ((1+ofs)%4)+1, 114, 52));
		addSlot(new OutputSlot(centrifuge, ((2+ofs)%4)+1, 76, 82));
		addSlot(new OutputSlot(centrifuge, ((3+ofs)%4)+1, 46, 44));
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
		
		addProperties(properties);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return centrifuge.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack out = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			out = slotStack.copy();
			if (index >= 1 && index <= 4) {
				// try to put outputs into the player's inventory
				if (!insertItem(slotStack, 6, 42, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(slotStack, out);
			} else if (index > 5) {
				if (isCentrifugable(slotStack)) {
					// try to put valid inputs into the input slot
					if (!insertItem(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (isFuel(slotStack)) {
					// try to put valid fuels into the fuel slot
					if (!insertItem(slotStack, 5, 6, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 6 && index < 33) {
					// move main inventory to hotbar
					if (!insertItem(slotStack, 33, 42, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 33 && index < 42 && !insertItem(slotStack, 6, 33, false)) {
					// move hotbar to main inventory
					return ItemStack.EMPTY;
				}
			} else if (!insertItem(slotStack, 6, 42, false)) {
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

	protected boolean isCentrifugable(ItemStack stack) {
		return world.getRecipeManager().getFirstMatch(YRecipeTypes.CENTRIFUGING, new SimpleInventory(stack), world).isPresent();
	}

	protected boolean isFuel(ItemStack stack) {
		return FurnaceBlockEntity.canUseAsFuel(stack);
	}
	
	public int getSpinTime() {
		return properties.get(2);
	}
	
	public int getMaxSpinTime() {
		int time = properties.get(3);
		if (time == 0) return 200;
		return time;
	}

	public int getFuelTime() {
		return properties.get(0);
	}
	
	public int getMaxFuelTime() {
		return properties.get(1);
	}

	public boolean isBurning() {
		return properties.get(0) > 0;
	}

}
