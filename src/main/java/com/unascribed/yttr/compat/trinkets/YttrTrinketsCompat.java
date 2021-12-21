package com.unascribed.yttr.compat.trinkets;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.conditional.YTrinkets;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Identifier;

public class YttrTrinketsCompat {
	
	public static void init() {
		Yttr.getSoleTrinket = pe -> TrinketsApi.getTrinketComponent(pe).getStack(SlotGroups.FEET, YTrinkets.SOLE);
		Yttr.setSoleTrinket = (pe, is) -> {
			int i = 0;
			for (String s : TrinketSlots.getAllSlotNames()) {
				if (s.equals(SlotGroups.FEET+":"+YTrinkets.SOLE)) {
					TrinketsApi.getTrinketComponent(pe).getInventory().setStack(i, is);
					break;
				}
				i++;
			}
		};
		Yttr.getBackTrinket = pe -> TrinketsApi.getTrinketComponent(pe).getStack(SlotGroups.CHEST, Slots.BACKPACK);
		
		TrinketSlots.addSlot(SlotGroups.FEET, YTrinkets.SOLE, new Identifier("trinkets", "textures/item/empty_trinket_slot_sole.png"));
		TrinketSlots.addSlot(SlotGroups.CHEST, Slots.BACKPACK, new Identifier("trinkets", "textures/item/empty_trinket_slot_backpack.png"));
	}
	
}
