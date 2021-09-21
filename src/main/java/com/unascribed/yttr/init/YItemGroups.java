package com.unascribed.yttr.init;

import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.mechanics.LampColor;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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
	public static final ItemGroup POTION = FabricItemGroupBuilder.create(new Identifier("yttr", "potion"))
		.icon(() -> PotionUtil.setPotion(new ItemStack(YItems.MERCURIAL_POTION), Potions.SWIFTNESS))
		.build();
	public static final ItemGroup RUINED = FabricItemGroupBuilder.create(new Identifier("yttr", "ruined"))
			.icon(() -> new ItemStack(YItems.WASTELAND_GRASS))
			.build();

	public static void init() {
		
	}

}
