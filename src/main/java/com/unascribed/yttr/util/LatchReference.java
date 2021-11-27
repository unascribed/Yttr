package com.unascribed.yttr.util;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.google.common.base.Objects;

/**
 * An Optional you can set later.
 */
public class LatchReference<T> {

	private boolean set;
	private boolean present;
	private T value;
	
	private LatchReference() {}
	
	/**
	 * Set this latch to the given value.
	 * @throws IllegalStateException if the latch has already been set
	 */
	public void set(T value) {
		if (set) throw new IllegalStateException("Latch already set");
		this.set = true;
		this.present = true;
		this.value = value;
	}
	
	/**
	 * Set this latch to empty.
	 * @throws IllegalStateException if the latch has already been set
	 */
	public void setEmpty() {
		if (set) throw new IllegalStateException("Latch already set");
		this.set = true;
		this.present = false;
		this.value = null;
	}
	
	/**
	 * If this latch is unset, sets it to empty. Otherwise, does nothing.
	 * <p>
	 * Allows "freezing" the current state of the latch.
	 */
	public void latch() {
		if (!isSet()) setEmpty();
	}
	
	/**
	 * @return {@code true} if this latch has been set
	 */
	public boolean isSet() {
		return set;
	}
	
	/**
	 * @return {@code true} if this latch has been set and has a value
	 */
	public boolean isPresent() {
		return set && present;
	}
	
	/**
	 * Retrieve the value held by this latch.
	 * @throws IllegalStateException if the latch has not been set
	 * @throws NoSuchElementException if the latch is set and empty
	 */
	public T get() {
		if (!isSet()) throw new IllegalStateException("Latch has not been set");
		if (!isPresent()) throw new NoSuchElementException("Latch is empty");
		return value;
	}
	
	/**
	 * Retrieve the value held by this latch, if it is set and present, otherwise the passed value.
	 */
	public T orElse(T t) {
		return isPresent() ? get() : t;
	}
	
	/**
	 * Test the value of this latch with the given predicate if the latch is set and present.
	 * Otherwise, returns false.
	 */
	public boolean test(Predicate<T> pred) {
		return isPresent() ? pred.test(get()) : false;
	}
	
	/**
	 * @return {@code true} if this latch is set and present, and the value is equal to the passed value
	 */
	public boolean is(Object t) {
		return isPresent() && Objects.equal(get(), t);
	}
	
	/**
	 * @return an unset latch
	 */
	public static <T> LatchReference<T> unset() {
		return new LatchReference<>();
	}
	
	/**
	 * @return a set empty latch
	 */
	public static <T> LatchReference<T> empty() {
		LatchReference<T> lr = new LatchReference<>();
		lr.setEmpty();
		return lr;
	}
	
	/**
	 * @return a set latch with the given value
	 */
	public static <T> LatchReference<T> of(T t) {
		LatchReference<T> lr = new LatchReference<>();
		lr.set(t);
		return lr;
	}
	
}
