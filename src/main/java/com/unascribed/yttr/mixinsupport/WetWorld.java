package com.unascribed.yttr.mixinsupport;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;

public interface WetWorld {

	Multimap<BlockPos, ItemEntity> yttr$getSoakingMap();
	Table<BlockPos, Fluid, Integer> yttr$getTimeTable();

}
