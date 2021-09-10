package com.unascribed.yttr.init;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class YTags {

	public static class Item {

		public static final Tag<net.minecraft.item.Item> UNSNAREABLE = TagRegistry.item(new Identifier("yttr", "unsnareable"));
		public static final Tag<net.minecraft.item.Item> VOID_IMMUNE = TagRegistry.item(new Identifier("yttr", "void_immune"));
		public static final Tag<net.minecraft.item.Item> FLUXES = TagRegistry.item(new Identifier("yttr", "fluxes"));
		
		private static void init() {}
		
	}
	
	public static class Block {

		public static final Tag<net.minecraft.block.Block> FIRE_MODE_INSTABREAK = TagRegistry.block(new Identifier("yttr", "fire_mode_instabreak"));
		public static final Tag<net.minecraft.block.Block> SNAREABLE = TagRegistry.block(new Identifier("yttr", "snareable"));
		public static final Tag<net.minecraft.block.Block> UNSNAREABLE = TagRegistry.block(new Identifier("yttr", "unsnareable"));
		public static final Tag<net.minecraft.block.Block> UNCLEAVABLE = TagRegistry.block(new Identifier("yttr", "uncleavable"));
		
		private static void init() {}
		
	}
	
	public static class Fluid {

		public static final Tag<net.minecraft.fluid.Fluid> VOID = AccessorFluidTags.getRequiredTags().add("yttr:void");
		public static final Tag<net.minecraft.fluid.Fluid> PURE_VOID = AccessorFluidTags.getRequiredTags().add("yttr:pure_void");
		
		private static void init() {}
		
	}
	
	public static class Entity {

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
