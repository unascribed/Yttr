package com.unascribed.yttr.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ItemEntityRenderer;

public class DiscOfContinuityRenderer extends ItemEntityRenderer {

	public DiscOfContinuityRenderer(EntityRenderDispatcher dispatcher) {
		super(dispatcher, MinecraftClient.getInstance().getItemRenderer());
	}

}
