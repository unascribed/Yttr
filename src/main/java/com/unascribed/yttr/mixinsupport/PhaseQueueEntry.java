package com.unascribed.yttr.mixinsupport;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class PhaseQueueEntry {

	public final int lifetime;
	public int delayLeft;
	@Nullable
	public UUID owner;
	
	public PhaseQueueEntry(int lifetime, int delayLeft, UUID owner) {
		this.lifetime = lifetime;
		this.delayLeft = delayLeft;
		this.owner = owner;
	}
	
}
