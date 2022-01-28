package com.unascribed.yttr.compat.rei;

import com.unascribed.yttr.inventory.ProjectTableScreenHandler;

import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.containers.CraftingContainerInfoWrapper;
import me.shedaniel.rei.server.ContainerInfoHandler;

public class YttrREIServerPlugin implements Runnable {

	@Override
	public void run() {
		ContainerInfoHandler.registerContainerInfo(DefaultPlugin.CRAFTING, CraftingContainerInfoWrapper.create(ProjectTableScreenHandler.class));
	}
	
}
