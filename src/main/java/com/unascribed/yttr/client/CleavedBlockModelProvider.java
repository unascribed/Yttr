package com.unascribed.yttr.client;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class CleavedBlockModelProvider implements ModelResourceProvider {

	private static final Identifier ID = new Identifier("yttr", "builtin/cleaved_block");
	public static boolean initialized = false;
	
	public static void init() {
		Renderer r = RendererAccess.INSTANCE.getRenderer();
		if (r != null) {
			initialized = true;
			ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new CleavedBlockModelProvider());
		} else {
			LogManager.getLogger("Yttr").warn("No implementation of the Fabric Rendering API is available. Cleaved blocks won't render!");
			LogManager.getLogger("Yttr").warn("You're probably using Sodium; consider updating to a dev build and installing Indium.");
		}
	}

	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
		if (ID.equals(resourceId)) {
			return new CleavedBlockModel();
		}
		return null;
	}

}
