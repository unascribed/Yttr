package com.unascribed.yttr.init;

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

	public static final Block COPPER_ORE = new Block(Settings.copy(Blocks.IRON_ORE));
	
	public static final Item COPPER_INGOT = new Item(new Item.Settings());

	public static final ConfiguredFeature<?, ?> COPPER_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					COPPER_ORE.getDefaultState(),
					10))
			.rangeOf(96)
			.spreadHorizontally()
			.repeat(6);
	
	public static void init() {
		Registry.register(Registry.BLOCK, "yttr:copper_ore", COPPER_ORE);
		
		Registry.register(Registry.ITEM, "yttr:copper_ore", new BlockItem(COPPER_ORE, new Item.Settings()));
		
		Registry.register(Registry.ITEM, "yttr:copper_ingot", COPPER_INGOT);
		
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "yttr:copper_overworld", COPPER_OVERWORLD);
		
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, YWorldGen.key("copper_overworld"));
	}

}
