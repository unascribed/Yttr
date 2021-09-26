package com.unascribed.yttr.client;

import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.common.collect.Sets;

import net.minecraft.resource.metadata.ResourceMetadataReader;

public class RuinedRecipeResourceMetadataReader implements ResourceMetadataReader<RuinedRecipeResourceMetadata> {
	@Override
	public RuinedRecipeResourceMetadata fromJson(JsonObject jsonObject) {
		Set<Integer> emptySlots = Sets.newHashSet();
		if (jsonObject.has("emptySlots")) {
			for (JsonElement je : jsonObject.get("emptySlots").getAsJsonArray()) {
				emptySlots.add(je.getAsInt());
			}
		}
		return new RuinedRecipeResourceMetadata(emptySlots);
	}

	@Override
	public String getKey() {
		return "yttr:ruined_recipe";
	}
	

	
}
