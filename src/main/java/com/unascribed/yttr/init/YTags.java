package com.unascribed.yttr.init;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class YTags {

	public static final class Item {

		public static final Tag<net.minecraft.item.Item> UNSNAREABLE = TagRegistry.item(new Identifier("yttr", "unsnareable"));
		public static final Tag<net.minecraft.item.Item> VOID_IMMUNE = TagRegistry.item(new Identifier("yttr", "void_immune"));
		public static final Tag<net.minecraft.item.Item> FLUXES = TagRegistry.item(new Identifier("yttr", "fluxes"));
		public static final Tag<net.minecraft.item.Item> ULTRAPURE_CUBES = TagRegistry.item(new Identifier("yttr", "ultrapure_cubes"));
		public static final Tag<net.minecraft.item.Item> GIFTS = TagRegistry.item(new Identifier("yttr", "gifts"));
		public static final Tag<net.minecraft.item.Item> NOT_GIFTS = TagRegistry.item(new Identifier("yttr", "not_gifts"));
		public static final Tag<net.minecraft.item.Item> MAGNETIC = TagRegistry.item(new Identifier("yttr", "magnetic"));
		public static final Tag<net.minecraft.item.Item> CONDUCTIVE_BOOTS = TagRegistry.item(new Identifier("yttr", "conductive_boots"));
		
		private static void init() {}
		
	}
	
	public static final class Block {

		public static final Tag<net.minecraft.block.Block> FIRE_MODE_INSTABREAK = TagRegistry.block(new Identifier("yttr", "fire_mode_instabreak"));
		public static final Tag<net.minecraft.block.Block> SNAREABLE = TagRegistry.block(new Identifier("yttr", "snareable"));
		public static final Tag<net.minecraft.block.Block> UNSNAREABLE = TagRegistry.block(new Identifier("yttr", "unsnareable"));
		public static final Tag<net.minecraft.block.Block> UNCLEAVABLE = TagRegistry.block(new Identifier("yttr", "uncleavable"));
		public static final Tag<net.minecraft.block.Block> GIFTS = TagRegistry.block(new Identifier("yttr", "gifts"));
		public static final Tag<net.minecraft.block.Block> MAGTUBE_TARGETS = TagRegistry.block(new Identifier("yttr", "magtube_targets"));
		public static final Tag<net.minecraft.block.Block> RUINED_DEVICES = TagRegistry.block(new Identifier("yttr", "ruined_devices"));
		public static final Tag<net.minecraft.block.Block> ORES = TagRegistry.block(new Identifier("yttr", "ores"));
		public static final Tag<net.minecraft.block.Block> LESSER_ORES = TagRegistry.block(new Identifier("yttr", "lesser_ores"));
		public static final Tag<net.minecraft.block.Block> CLAMBER_BLOCKS = TagRegistry.block(new Identifier("yttr", "clamber_blocks"));
		public static final Tag<net.minecraft.block.Block> MAGNETIC = TagRegistry.block(new Identifier("yttr", "magnetic"));
		
		private static void init() {}
		
	}
	
	public static final class Fluid {

		public static final Tag<net.minecraft.fluid.Fluid> VOID = AccessorFluidTags.getRequiredTags().add("yttr:void");
		public static final Tag<net.minecraft.fluid.Fluid> PURE_VOID = AccessorFluidTags.getRequiredTags().add("yttr:pure_void");
		
		private static void init() {}
		
	}
	
	public static final class Entity {

		public static final Tag<EntityType<?>> UNSNAREABLE = TagRegistry.entityType(new Identifier("yttr", "unsnareable"));
		public static final Tag<EntityType<?>> SNAREABLE_NONLIVING = TagRegistry.entityType(new Identifier("yttr", "snareable_nonliving"));
		public static final Tag<EntityType<?>> BOSSES = TagRegistry.entityType(new Identifier("yttr", "bosses"));
		
		private static void init() {}
		
	}

	public static void init() {
		Item.init();
		Block.init();
		Fluid.init();
		Entity.init();
	}
	
}
