package com.unascribed.yttr.mixinsupport;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.Nullable;

public class PhaseQueueEntry {

	public final int lifetime;
	public final AtomicInteger delayLeft;
	@Nullable
	public final UUID owner;
	
	public PhaseQueueEntry(int lifetime, int delayLeft, UUID owner) {
		this.lifetime = lifetime;
		this.delayLeft = new AtomicInteger(delayLeft);
		this.owner = owner;
	}
	
}
