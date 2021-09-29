package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YHandledScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MagtankScreenHandler extends ScreenHandler {

	public MagtankScreenHandler(int syncId, PlayerInventory playerInv) {
		super(YHandledScreens.MAGTANK, syncId);
		addProperties(new ArrayPropertyDelegate(2));
	}
	
	public MagtankScreenHandler(ServerWorld world, BlockPos pos, int syncId, PlayerInventory playerInv) {
		super(YHandledScreens.MAGTANK, syncId);
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 112 + y * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlot(new Slot(playerInv, i, 8 + i * 18, 112 + 58));
		}
		
		addProperties(new PropertyDelegate() {
			
			@Override
			public int size() {
				return 2;
			}
			
			@Override
			public void set(int index, int value) {
				
			}
			
			@Override
			public int get(int index) {
				return 0;
			}
		});
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

}
