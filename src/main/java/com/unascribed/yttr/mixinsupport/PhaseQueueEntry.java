package com.unascribed.yttr.mixinsupport;

public class PhaseQueueEntry {

	public final int lifetime;
	public int delayLeft;
	
	public PhaseQueueEntry(int lifetime, int delayLeft) {
		this.lifetime = lifetime;
		this.delayLeft = delayLeft;
	}
	
}
