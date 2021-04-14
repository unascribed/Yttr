package com.unascribed.yttr.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

public interface SideyInventory extends SidedInventory {

	IntList scratchList = new IntArrayList();
	
	boolean canAccess(int slot, Direction side);
	
	@Override
	default int[] getAvailableSlots(Direction side) {
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
