package com.unascribed.yttr.init.conditional;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YWorldGen;
import com.unascribed.yttr.util.annotate.RegisteredAs;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

@SuppressWarnings("deprecation")
public class YCopper {

	private static final Block COPPER_ORE = new Block(Settings.copy(Blocks.IRON_ORE));
	@RegisteredAs("copper_ore")
	private static final BlockItem COPPER_ORE_ITEM = new BlockItem(COPPER_ORE, new Item.Settings());
	
	private static final Item COPPER_INGOT = new Item(new Item.Settings());

	private static final ConfiguredFeature<?, ?> COPPER_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					COPPER_ORE.getDefaultState(),
					10))
			.rangeOf(96)
			.spreadHorizontally()
			.repeat(6);
	
	public static void init() {
		Yttr.autoRegister(Registry.BLOCK, YCopper.class, Block.class);
		Yttr.autoRegister(Registry.ITEM, YCopper.class, Item.class);
		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_FEATURE, YCopper.class, ConfiguredFeature.class);
		
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, YWorldGen.key("copper_overworld"));
		
		YBlocks.COPPER_ORE.set(COPPER_ORE);
		YItems.COPPER_ORE.set(COPPER_ORE_ITEM);
		YItems.COPPER_INGOT.set(COPPER_INGOT);
		YWorldGen.COPPER_OVERWORLD.set(COPPER_OVERWORLD);
	}

}
