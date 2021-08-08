package com.unascribed.yttr.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

public interface SideyInventory extends SidedInventory {

	ThreadLocal<IntList> scratchList = ThreadLocal.withInitial(IntArrayList::new);
	
	boolean canAccess(int slot, Direction side);
	
	@Override
	default int[] getAvailableSlots(Direction side) {
		IntList scratchList = SideyInventory.scratchList.get();
		scratchList.clear();
		for (int i = 0; i < size(); i++) {
			if (canAccess(i, side)) {
				scratchList.add(i);
			}
		}
		int[] rtrn = scratchList.toIntArray();
		scratchList.clear();
		return rtrn;
	}
	
}
