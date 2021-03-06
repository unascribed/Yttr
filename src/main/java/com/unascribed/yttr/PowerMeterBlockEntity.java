package com.unascribed.yttr;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class PowerMeterBlockEntity extends BlockEntity {

	public int readout;
	public long readoutTime = 0;
	
	public PowerMeterBlockEntity() {
		super(Yttr.POWER_METER_ENTITY);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag tag = super.toInitialChunkDataTag();
		tag.putShort("Readout", (short)readout);
		return tag;
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		readout = (type << 8) | data;
		readoutTime = System.currentTimeMillis();
		return true;
	}

	public void sendReadout(int readout) {
		this.readout = readout;
		this.readoutTime = System.currentTimeMillis();
		getWorld().addSyncedBlockEvent(getPos(), Yttr.POWER_METER, ((readout >> 8)&0xFF), readout&0xFF);
		getWorld().updateNeighborsAlways(pos, Yttr.POWER_METER);
		getWorld().getBlockTickScheduler().schedule(getPos(), Yttr.POWER_METER, 4*20);
	}
	

}
