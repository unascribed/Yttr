package com.unascribed.yttr.client;

import com.google.gson.JsonObject;
import com.unascribed.yttr.client.IBXMAudioStream.InterpolationMode;

import com.google.common.base.Ascii;

import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;

public class IBXMResourceMetadataReader implements ResourceMetadataReader<IBXMResourceMetadata> {
	@Override
	public IBXMResourceMetadata fromJson(JsonObject jsonObject) {
		return new IBXMResourceMetadata(
				jsonObject.has("mode") ? InterpolationMode.valueOf(Ascii.toUpperCase(JsonHelper.getString(jsonObject, "mode"))) : null,
				JsonHelper.getBoolean(jsonObject, "stereo", false)
			);
	}

	@Override
	public String getKey() {
		return "yttr:ibxm";
	}
	

	
}
