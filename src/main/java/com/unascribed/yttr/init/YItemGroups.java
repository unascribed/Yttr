package com.unascribed.yttr.init;

import com.unascribed.yttr.block.decor.LampBlock;
import com.unascribed.yttr.mechanics.LampColor;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class YItemGroups {

	public static final ItemGroup MAIN = FabricItemGroupBuilder.create(new Identifier("yttr", "main"))
		.icon(() -> new ItemStack(YItems.LOGO))
		.build();
	public static final ItemGroup SNARE = FabricItemGroupBuilder.create(new Identifier("yttr", "snare"))
		.icon(() -> new ItemStack(YItems.SNARE))
		.build();
	public static final ItemGroup LAMP = FabricItemGroupBuilder.create(new Identifier("yttr", "lamp"))
		.icon(() -> YBlocks.LAMP.getPickStack(null, null, YBlocks.LAMP.getDefaultState().with(LampBlock.COLOR, LampColor.CYAN).with(LampBlock.INVERTED, true)))
		.build();

	public static void init() {
		
	}

}
