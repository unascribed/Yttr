package com.unascribed.yttr.mixin.effector;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.YttrWorld;
import com.unascribed.yttr.mixinsupport.PhaseQueueEntry;

import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld implements YttrWorld {

	private final Map<BlockPos, MutableInt> yttr$phase = Maps.newHashMap();
	private final Map<BlockPos, UUID> yttr$phaseOwners = Maps.newHashMap();
	private final Map<BlockPos, PhaseQueueEntry> yttr$phaseQueue = Maps.newHashMap();
	
	
	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);
	@Shadow
	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	@Inject(at=@At("HEAD"), method="tickBlockEntities()V")
	public void tickBlockEntities(CallbackInfo ci) {
		if (!yttr$phase.isEmpty()) {
			Iterator<Map.Entry<BlockPos, MutableInt>> iter = yttr$phase.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<BlockPos, MutableInt> en = iter.next();
				if (en.getValue().decrementAndGet() <= 0) {
					iter.remove();
					yttr$phaseOwners.remove(en.getKey());
					yttr$scheduleRenderUpdate(en.getKey());
				}
			}
		}
		if (!yttr$phaseQueue.isEmpty()) {
			Iterator<Map.Entry<BlockPos, PhaseQueueEntry>> iter = yttr$phaseQueue.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<BlockPos, PhaseQueueEntry> en = iter.next();
				if (en.getValue().delayLeft-- <= 0) {
					iter.remove();
					yttr$addPhaseBlock(en.getKey(), en.getValue().lifetime, -1, en.getValue().owner);
				}
			}
		}
	}
	
	@Override
	public void yttr$scheduleRenderUpdate(BlockPos pos) {
	}
	
	@Override
	public boolean yttr$isPhased(BlockPos pos) {
		return yttr$phase.containsKey(pos);
	}
	
	@Override
	public @Nullable UUID yttr$getPhaser(BlockPos pos) {
		return yttr$phaseOwners.get(pos);
	}

	@Override
	public void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay, UUID owner) {
		BlockPos imm = pos.toImmutable();
		if (delay <= 0) {
			yttr$phase.put(imm, new MutableInt(lifetime));
			if (owner != null) yttr$phaseOwners.put(imm, owner);
			yttr$scheduleRenderUpdate(pos);
		} else {
			yttr$phaseQueue.put(imm, new PhaseQueueEntry(lifetime, delay, owner));
		}
	}

	@Override
	public void yttr$removePhaseBlock(BlockPos pos) {
		yttr$phase.remove(pos);
	}
	
	
}
