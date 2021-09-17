package com.unascribed.yttr.client;

import com.unascribed.yttr.client.IBXMAudioStream.InterpolationMode;

public class IBXMResourceMetadata {
	public static final IBXMResourceMetadataReader READER = new IBXMResourceMetadataReader();
	private final boolean stereo;
	private final InterpolationMode mode;

	public IBXMResourceMetadata(InterpolationMode mode, boolean stereo) {
		this.mode = mode;
		this.stereo = stereo;
	}

	public boolean isStereo() {
		return stereo;
	}
	
	public InterpolationMode getMode() {
		return mode;
	}

}
