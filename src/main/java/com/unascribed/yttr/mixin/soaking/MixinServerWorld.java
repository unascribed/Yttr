package com.unascribed.yttr.mixin.soaking;

import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.yttr.mixinsupport.WetWorld;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ServerWorld.class)
public class MixinServerWorld implements WetWorld {

	private final Multimap<BlockPos, ItemEntity> yttr$soakingMap = HashMultimap.create();
	private final Table<BlockPos, Fluid, Integer> yttr$timeTable = HashBasedTable.create();
	
	@Override
	public Multimap<BlockPos, ItemEntity> yttr$getSoakingMap() {
		return yttr$soakingMap;
	}
	
	@Override
	public Table<BlockPos, Fluid, Integer> yttr$getTimeTable() {
		return yttr$timeTable;
	}
	
}
