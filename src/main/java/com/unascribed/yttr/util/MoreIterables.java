package com.unascribed.yttr.util;

import java.util.function.Predicate;

public class MoreIterables {

	public static <T> int count(Iterable<T> iter, Predicate<T> pred) {
		int i = 0;
		for (T t : iter) {
			if (pred.test(t)) i++;
		}
		return i;
	}
	
}
