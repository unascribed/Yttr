package com.unascribed.yttr.compat.trinkets;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.Yttr.TrinketsAccess;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.init.conditional.YTrinkets;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class YttrTrinketsCompat {
	
	public static void init() {
		Yttr.trinketsAccess = new TrinketsAccess() {
			
			@Override
			public ItemStack getSoleTrinket(PlayerEntity pe) {
				return TrinketsApi.getTrinketComponent(pe).getStack(SlotGroups.FEET, YTrinkets.SOLE);
			}
			
			@Override
			public ItemStack getBackTrinket(PlayerEntity pe) {
				return TrinketsApi.getTrinketComponent(pe).getStack(SlotGroups.CHEST, Slots.BACKPACK);
			}
			
			@Override
			public void dropMagneticTrinkets(PlayerEntity pe) {
				Inventory inv = TrinketsApi.getTrinketsInventory(pe);
				for (int i = 0; i < inv.size(); i++) {
					if (inv.getStack(i).getItem().isIn(YTags.Item.MAGNETIC)) {
						ItemEntity ie = pe.dropStack(inv.removeStack(i));
						if (ie != null) ie.setVelocity(0, -1, 0);
					}
				}
			}
		};
		
		TrinketSlots.addSlot(SlotGroups.FEET, YTrinkets.SOLE, new Identifier("trinkets", "textures/item/empty_trinket_slot_sole.png"));
		TrinketSlots.addSlot(SlotGroups.CHEST, Slots.BACKPACK, new Identifier("trinkets", "textures/item/empty_trinket_slot_backpack.png"));
	}
	
}
