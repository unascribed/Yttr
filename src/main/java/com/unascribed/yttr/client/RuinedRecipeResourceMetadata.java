package com.unascribed.yttr.client;

import java.util.Set;

public class RuinedRecipeResourceMetadata {
	public static final RuinedRecipeResourceMetadataReader READER = new RuinedRecipeResourceMetadataReader();
	private final Set<Integer> emptySlots;

	public RuinedRecipeResourceMetadata(Set<Integer> emptySlots) {
		this.emptySlots = emptySlots;
	}

	public Set<Integer> getEmptySlots() {
		return emptySlots;
	}

}
