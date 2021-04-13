package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.init.YBlockEntities;
import net.minecraft.block.entity.BlockEntity;

public class SuitStationBlockEntity extends BlockEntity {

	public int lastCollisionTick;
	
	public SuitStationBlockEntity() {
		super(YBlockEntities.SUIT_STATION);
	}
	

}
