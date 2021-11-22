package com.unascribed.yttr.mixin.effector;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld implements YttrWorld {

	private final StampedLock yttr$phaseLock = new StampedLock();
	
	private final Map<BlockPos, AtomicInteger> yttr$phase = Maps.newHashMap();
	private final Map<BlockPos, UUID> yttr$phaseOwners = Maps.newHashMap();
	private final Map<BlockPos, PhaseQueueEntry> yttr$phaseQueue = Maps.newHashMap();
	
	
	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);
	@Shadow
	public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	@Inject(at=@At("HEAD"), method="tickBlockEntities()V")
	public void tickBlockEntities(CallbackInfo ci) {
		boolean writing = false;
		long stamp = yttr$phaseLock.readLock();
		try {
			if (!yttr$phase.isEmpty()) {
				Iterator<Map.Entry<BlockPos, AtomicInteger>> iter = yttr$phase.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<BlockPos, AtomicInteger> en = iter.next();
					if (en.getValue().decrementAndGet() <= 0) {
						if (!writing) {
							long wstamp = yttr$phaseLock.tryConvertToWriteLock(stamp);
							if (wstamp == 0) {
								yttr$phaseLock.unlockRead(stamp);
								stamp = yttr$phaseLock.writeLock();
							} else {
								stamp = wstamp;
							}
							writing = true;
						}
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
					if (en.getValue().delayLeft.decrementAndGet() <= 0) {
						if (!writing) {
							long wstamp = yttr$phaseLock.tryConvertToWriteLock(stamp);
							if (wstamp == 0) {
								yttr$phaseLock.unlockRead(stamp);
								stamp = yttr$phaseLock.writeLock();
							} else {
								stamp = wstamp;
							}
							writing = true;
						}
						iter.remove();
						yttr$addPhaseBlock(en.getKey(), en.getValue().lifetime, -1, en.getValue().owner);
					}
				}
			}
		} finally {
			yttr$phaseLock.unlock(stamp);
		}
	}
	
	@Override
	public void yttr$scheduleRenderUpdate(BlockPos pos) {
	}
	
	private final ThreadLocal<BlockPos.Mutable> yttr$scratchPos = ThreadLocal.withInitial(BlockPos.Mutable::new);
	
	@Override
	public boolean yttr$isPhased(ChunkPos chunkPos, BlockPos pos) {
		if (yttr$unmasked) return false;
		return yttr$isPhased(yttr$scratchPos.get().set(chunkPos.getStartX(), 0, chunkPos.getStartZ()).move(pos.getX()&15, pos.getY(), pos.getZ()&15));
	}
	
	@Override
	public boolean yttr$isPhased(int x, int y, int z) {
		return yttr$isPhased(yttr$scratchPos.get().set(x, y, z));
	}
	
	@Override
	public boolean yttr$isPhased(BlockPos pos) {
		long stamp = yttr$phaseLock.tryOptimisticRead();
		boolean phased = yttr$phase.containsKey(pos);
		if (!yttr$phaseLock.validate(stamp)) {
			stamp = yttr$phaseLock.readLock();
			try {
				phased = yttr$phase.containsKey(pos);
			} finally {
				yttr$phaseLock.unlockRead(stamp);
			}
		}
		return phased;
	}
	
	@Override
	public @Nullable UUID yttr$getPhaser(BlockPos pos) {
		long stamp = yttr$phaseLock.readLock();
		try {
			return yttr$phaseOwners.get(pos);
		} finally {
			yttr$phaseLock.unlockRead(stamp);
		}
	}

	@Override
	public void yttr$addPhaseBlock(BlockPos pos, int lifetime, int delay, UUID owner) {
		long stamp = yttr$phaseLock.writeLock();
		try {
			BlockPos imm = pos.toImmutable();
			if (delay <= 0) {
				yttr$phase.put(imm, new AtomicInteger(lifetime));
				if (owner != null) yttr$phaseOwners.put(imm, owner);
				yttr$scheduleRenderUpdate(pos);
			} else {
				yttr$phaseQueue.put(imm, new PhaseQueueEntry(lifetime, delay, owner));
			}
		} finally {
			yttr$phaseLock.unlockWrite(stamp);
		}
	}

	@Override
	public void yttr$removePhaseBlock(BlockPos pos) {
		long stamp = yttr$phaseLock.writeLock();
		try {
			yttr$phase.remove(pos);
		} finally {
			yttr$phaseLock.unlockWrite(stamp);
		}
	}
	
	private boolean yttr$unmasked;
	
	@Override
	public void yttr$setUnmask(boolean unmask) {
		this.yttr$unmasked = unmask;
	}
	
}
