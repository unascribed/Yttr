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
		
		TrinketSlots.addSlot(SlotGroups.FEET, YTrinkets.SOLE, new Identifier("trinkets", "textures/item/empty_trinket_slot_sole.png"));
		TrinketSlots.addSlot(SlotGroups.CHEST, Slots.BACKPACK, new Identifier("trinkets", "textures/item/empty_trinket_slot_backpack.png"));
	}
	
}
