package com.unascribed.yttr.inventory;

import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.content.block.device.SuitStationBlockEntity;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class SuitStationScreenHandler extends ScreenHandler {

	public class ArmorSlot extends Slot {
		
		private final EquipmentSlot slot;
		private final Identifier icon;
		
		public ArmorSlot(Inventory inventory, int index, int x, int y, EquipmentSlot slot, Identifier icon) {
			super(inventory, index, x, y);
			this.slot = slot;
			this.icon = icon;
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return MobEntity.getPreferredEquipmentSlot(stack) == slot;
		}

		@Override
		public boolean canTakeItems(PlayerEntity player) {
			ItemStack stack = getStack();
			return !stack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(stack) ? false : super.canTakeItems(player);
		}

		@Override
		@Environment(EnvType.CLIENT)
		public Pair<Identifier, Identifier> getBackgroundSprite() {
			return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, icon);
		}
	}
	
	public class SuitSlot extends Slot {

		private final EquipmentSlot slot;
		
		public SuitSlot(Inventory inventory, int index, int x, int y, EquipmentSlot slot) {
			super(inventory, index, x, y);
			this.slot = slot;
		}
		
		@Override
		public boolean canInsert(ItemStack stack) {
			return SuitStationBlockEntity.isSuit(stack, slot);
		}

	}

	private final Inventory station;
	private final PropertyDelegate properties;
	
	public SuitStationScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(8), syncId, playerInv, new ArrayPropertyDelegate(4));
	}
	
	public SuitStationScreenHandler(Inventory station, int syncId, PlayerInventory playerInv, PropertyDelegate properties) {
		super(YHandledScreens.SUIT_STATION, syncId);
		
		this.station = station;
		this.properties = properties;
		
		addSlot(new SuitSlot(station, 0, 129,  9, EquipmentSlot.HEAD));
		addSlot(new SuitSlot(station, 1, 129, 27, EquipmentSlot.CHEST));
		addSlot(new SuitSlot(station, 2, 129, 45, EquipmentSlot.LEGS));
		addSlot(new SuitSlot(station, 3, 129, 63, EquipmentSlot.FEET));
		
		addSlot(new Slot(station, 4, 33, 27) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == Items.GLOWSTONE_DUST;
			}
		});
		addSlot(new Slot(station, 5, 33, 63) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return FurnaceBlockEntity.canUseAsFuel(stack);
			}
		});
		
		addSlot(new Slot(station, 6, 77, 27) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == YItems.ARMOR_PLATING;
			}
		});
		addSlot(new Slot(station, 7, 77, 63) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem().isIn(YTags.Item.FLUXES);
			}
		});
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 33 + x * 18, 99 + y * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInv, i, 33 + i * 18, 157));
		}
		
		addSlot(new ArmorSlot(playerInv, 39, 8,  99, EquipmentSlot.HEAD, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE));
		addSlot(new ArmorSlot(playerInv, 38, 8, 117, EquipmentSlot.CHEST, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE));
		addSlot(new ArmorSlot(playerInv, 37, 8, 135, EquipmentSlot.LEGS, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE));
		addSlot(new ArmorSlot(playerInv, 36, 8, 153, EquipmentSlot.FEET, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE));
		
		addProperties(properties);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return station.canPlayerUse(player);
	}
	
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack out = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			out = slotStack.copy();
			if (index < 4) {
				// try to put the suit into the player's armor slots, or inventory
				if (!insertItem(slotStack, 8, 48, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 4 && index <= 7) {
				// try to put resources into the player's inventory
				if (!insertItem(slotStack, 8, 43, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(slotStack, out);
			} else if (index > 5) {
				if (slotStack.getItem() instanceof SuitArmorItem) {
					// try to put suit pieces into the suit slots
					if (!insertItem(slotStack, 0, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (isFuel(slotStack)) {
					// try to put valid fuels into the fuel slot
					if (!insertItem(slotStack, 5, 6, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStack.getItem().isIn(YTags.Item.FLUXES)) {
					// try to put valid fluxes into the flux slot
					if (!insertItem(slotStack, 7, 8, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStack.getItem() == Items.GLOWSTONE_DUST) {
					// try to put glowstone into the glowstone slot
					if (!insertItem(slotStack, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStack.getItem() == YItems.ARMOR_PLATING) {
					// try to put plating into the plating slot
					if (!insertItem(slotStack, 6, 7, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 8 && index < 35) {
					// move main inventory to hotbar
					if (!insertItem(slotStack, 35, 44, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 35 && index < 44 && !insertItem(slotStack, 8, 35, false)) {
					// move hotbar to main inventory
					return ItemStack.EMPTY;
				}
			} else if (!insertItem(slotStack, 8, 43, false)) {
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

	protected boolean isFuel(ItemStack stack) {
		return FurnaceBlockEntity.canUseAsFuel(stack);
	}
	
	public int getFluxLeft() {
		return properties.get(2);
	}
	
	public int getMaxFluxLeft() {
		int time = properties.get(3);
		if (time == 0) return 200;
		return time;
	}

	public int getFuelTime() {
		return properties.get(0);
	}
	
	public int getMaxFuelTime() {
		int time = properties.get(1);
		if (time == 0) return 200;
		return time;
	}

}
