package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.inred.InRedDevice;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public abstract class InRedModuleBlockEntity extends BlockEntity {

	public InRedModuleBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	/*@Nullable*/
	public abstract InRedDevice getComponent(Direction inspectingFrom);
}
