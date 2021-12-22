package com.unascribed.yttr.util;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A wrapper for a Set that automatically promotes itself when it has a value added to it.
 * <p>
 * Avoids overhead for code paths that may commonly end up with an empty collection.
 */
public class MysticSet<E> {

	public static final MysticSet<Object> EMPTY = new MysticSet<>(Collections.EMPTY_SET);
	
	/**
	 * Returns the shared empty MysticSet, which will promote itself when needed by returning
	 * a new value from {@link #add}.
	 */
	public static <E> MysticSet<E> of() {
		return (MysticSet<E>)EMPTY;
	}
	
	private final Set<E> delegate;

	private MysticSet(Set<E> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Possibly promote this MysticSet, and then add the given value to it.
	 * @param val the value to add
	 * @return the new MysticSet; may be {@code this}, may be a new object
	 */
	public MysticSet<E> add(E val) {
		if (delegate.isEmpty()) {
			return new MysticSet<>(Sets.newHashSet(val));
		}
		delegate.add(val);
		return this;
	}
	
	/**
	 * @return this MysticSet's inner Set
	 */
	public Set<E> mundane() {
		return delegate;
	}
	
}
