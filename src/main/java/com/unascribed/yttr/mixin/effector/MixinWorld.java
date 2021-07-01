package com.unascribed.yttr.mixin.effector;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.YttrWorld;
import com.unascribed.yttr.mixinsupport.PhaseQueueEntry;

import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinWorld implements YttrWorld {

	private final Map<BlockPos, MutableInt> yttr$phase = Maps.newHashMap();
	private final Map<BlockPos, PhaseQueueEntry> yttr$phaseQueue = Maps.newHashMap();

	@Inject(at=@At("HEAD"), method="tickBlockEntities()V")
	public void tickBlockEntities(CallbackInfo ci) {
		if (!yttr$phase.isEmpty()) {
			Iterator<Map.Entry<BlockPos, MutableInt>> iter = yttr$phase.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<BlockPos, MutableInt> en = iter.next();
				if (en.getValue().decrementAndGet() <= 0) {
					iter.remove();
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
					yttr$addPhaseBlock(en.getKey(), en.getValue().lifetime, -1);
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
	public void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay) {
		if (delay <= 0) {
			yttr$phase.put(pos.toImmutable(), new MutableInt(lifetime));
			yttr$scheduleRenderUpdate(pos);
		} else {
			yttr$phaseQueue.put(pos.toImmutable(), new PhaseQueueEntry(lifetime, delay));
		}
	}

	@Override
	public void yttr$removePhaseBlock(BlockPos pos) {
		yttr$phase.remove(pos);
	}
	
	
}
