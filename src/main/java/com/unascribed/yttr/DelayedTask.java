package com.unascribed.yttr;

public class DelayedTask {

	public int delay;
	public final Runnable r;
	
	public DelayedTask(int delay, Runnable r) {
		this.delay = delay;
		this.r = r;
	}
	
	
}
