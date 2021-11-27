package com.unascribed.yttr.init;

import java.util.List;

import com.unascribed.yttr.util.LatchReference;

import com.google.common.collect.Lists;

public class YLatches {

	private static final List<LatchReference<?>> latches = Lists.newArrayList();
	
	public static <T> LatchReference<T> register(LatchReference<T> ref) {
		latches.add(ref);
		return ref;
	}
	
	public static void latchAll() {
		latches.forEach(LatchReference::latch);
		latches.clear();
	}
	
	public static <T> LatchReference<T> create() {
		return register(LatchReference.unset());
	}
	
}
