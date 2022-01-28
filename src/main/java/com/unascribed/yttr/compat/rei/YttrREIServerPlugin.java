package com.unascribed.yttr.compat.rei;

import java.util.List;

import com.unascribed.yttr.inventory.ProjectTableScreenHandler;

import com.google.common.collect.Lists;

import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.containers.CraftingContainerInfoWrapper;
import me.shedaniel.rei.server.ContainerContext;
import me.shedaniel.rei.server.ContainerInfoHandler;
import me.shedaniel.rei.server.StackAccessor;

public class YttrREIServerPlugin implements Runnable {

	@Override
	public void run() {
		ContainerInfoHandler.registerContainerInfo(DefaultPlugin.CRAFTING, new CraftingContainerInfoWrapper<ProjectTableScreenHandler>(ProjectTableScreenHandler.class) {
			@Override
			public List<StackAccessor> getInventoryStacks(ContainerContext<ProjectTableScreenHandler> context) {
				List<StackAccessor> li = Lists.newArrayList();
				for (int i = 10; i < 28; i++) {
					li.add(context.getStack(i));
				}
				li.addAll(super.getInventoryStacks(context));
				return li;
			}
		});
	}
	
}
