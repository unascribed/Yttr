package com.unascribed.yttr.content.block.decor;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mechanics.HaloBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;

public class LampBlockEntity extends BlockEntity implements HaloBlockEntity {

	public LampBlockEntity() {
		super(YBlockEntities.LAMP);
	}

	@Override
	public boolean shouldRenderHalo() {
		return getCachedState().get(LampBlock.LIT);
	}

	@Override
	public int getGlowColor() {
		return getCachedState().get(LampBlock.COLOR).glowColor;
	}
	
	@Override
	public @Nullable Direction getFacing() {
		return getCachedState().contains(WallLampBlock.FACING) ? getCachedState().get(WallLampBlock.FACING) : null;
	}
	
	@Override
	public Object getStateObject() {
		return getCachedState();
	}
	

}
