package com.unascribed.yttr;

import net.fabricmc.api.DedicatedServerModInitializer;

public class YttrServer implements DedicatedServerModInitializer {

	// dedicated server and client initializers are called after general initializers
	// we can use them to make up a "post-init" phase fabric lacks
	
	@Override
	public void onInitializeServer() {
		Yttr.INST.onPostInitialize();
	}

}
