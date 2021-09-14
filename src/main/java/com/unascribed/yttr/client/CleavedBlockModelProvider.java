package com.unascribed.yttr.client;

import com.unascribed.yttr.util.YLog;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class CleavedBlockModelProvider implements ModelResourceProvider {

	private static final Identifier ID = new Identifier("yttr", "builtin/cleaved_block");
	
	public static void init() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new CleavedBlockModelProvider());
	}

	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
		if (ID.equals(resourceId)) {
			if (!RendererAccess.INSTANCE.hasRenderer()) {
	 			YLog.warn("No implementation of the Fabric Rendering API was detected. Cleaved blocks likely won't render, and may crash the game!");
	 			YLog.warn("You're probably using Sodium; consider updating to a dev build and installing Indium.");
			}
			return new CleavedBlockModel();
		}
		return null;
	}

}
