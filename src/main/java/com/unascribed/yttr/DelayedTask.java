package com.unascribed.yttr;

public class DelayedTask {

	public int delay;
	public final Runnable r;
	public final boolean important;
	
	public DelayedTask(int delay, Runnable r) {
		this(delay, r, false);
	}

	public DelayedTask(int delay, Runnable r, boolean important) {
		this.delay = delay;
		this.r = r;
		this.important = important;
	}
	
}
