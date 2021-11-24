package com.unascribed.yttr.init;

import com.unascribed.yttr.ItemSubGroup;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mixin.accessor.AccessorItem;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YItemGroups {

	public static final ItemGroup PARENT = FabricItemGroupBuilder.create(new Identifier("yttr", "parent"))
		.icon(() -> new ItemStack(YItems.LOGO))
		.build()
		.hideName();
	public static final ItemSubGroup RESOURCES = ItemSubGroup.create(PARENT, new Identifier("yttr", "resources"));
	public static final ItemSubGroup FILTERING = ItemSubGroup.create(PARENT, new Identifier("yttr", "filtering"));
	public static final ItemSubGroup MECHANISMS = ItemSubGroup.create(PARENT, new Identifier("yttr", "mechanisms"));
	public static final ItemSubGroup EQUIPMENT = ItemSubGroup.create(PARENT, new Identifier("yttr", "equipment"));
	public static final ItemSubGroup SNARE = ItemSubGroup.create(PARENT, new Identifier("yttr", "snare"));
	public static final ItemSubGroup LAMP = ItemSubGroup.create(PARENT, new Identifier("yttr", "lamp"));
	public static final ItemSubGroup POTION = ItemSubGroup.create(PARENT, new Identifier("yttr", "potion"));
	public static final ItemSubGroup RUINED = ItemSubGroup.create(PARENT, new Identifier("yttr", "ruined"));
	public static final ItemSubGroup MISC = ItemSubGroup.create(PARENT, new Identifier("yttr", "misc"));

	public static void init() {
		Registry.ITEM.forEach(i -> {
			Identifier id = Registry.ITEM.getId(i);
			if (id != null && id.getNamespace().equals("yttr")) {
				ItemGroup group = MISC;
				if (i instanceof LampBlockItem) {
					group = LAMP;
				} else if (i instanceof PotionItem) {
					group = POTION;
				} else if (i instanceof BlockItem && ((BlockItem)i).getBlock().getLootTableId().equals(new Identifier("yttr", "blocks/ruined"))) {
					group = RUINED;
				} else if (id.getPath().startsWith("wasteland_")) {
					group = RUINED;
				} else if (id.getPath().startsWith("ultrapure_")) {
					group = FILTERING;
				}
				((AccessorItem)i).yttr$setGroup(group);
			}
		});
		assign(RESOURCES,
				YItems.GADOLINITE,
				YItems.YTTRIUM_BLOCK,
				YItems.SQUEEZE_LEAVES,
				YItems.SQUEEZE_LOG,
				YItems.SQUEEZE_SAPLING,
				YItems.STRIPPED_SQUEEZE_LOG,
				YItems.BROOKITE,
				YItems.BROOKITE_ORE,
				YItems.BROOKITE_BLOCK,
				YItems.YTTRIUM_INGOT,
				YItems.YTTRIUM_NUGGET,
				YItems.IRON_DUST,
				YItems.NEODYMIUM_DUST,
				YItems.YTTRIUM_DUST,
				YItems.XL_IRON_INGOT,
				YItems.DELICACE,
				YItems.GLOWING_GAS,
				YItems.NEODYMIUM_DISC,
				YItems.QUICKSILVER,
				YItems.BEDROCK_SHARD,
				YItems.GLASSY_VOID,
				YItems.VOID_BUCKET,
				YItems.ARMOR_PLATING,
				YItems.TABLE,
				YItems.ROOT_OF_CONTINUITY,
				YItems.DROP_OF_CONTINUITY,
				YItems.NETHERTUFF,
				YItems.GLASSY_VOID_PANE
			);
		assign(FILTERING,
				YItems.COMPRESSED_ULTRAPURE_CARBON,
				YItems.COMPRESSED_ULTRAPURE_CARBON_BLOCK,
				YItems.ENCASED_VOID_FILTER,
				YItems.VOID_FILTER,
				YItems.MAGTANK,
				YItems.MAGTUBE,
				YItems.DSU
			);
		assign(MECHANISMS,
				YItems.POWER_METER,
				YItems.CHUTE,
				YItems.HEAVY_YTTRIUM_PLATE,
				YItems.LIGHT_YTTRIUM_PLATE,
				YItems.DOPPER,
				YItems.FLOPPER,
				YItems.YTTRIUM_BUTTON,
				YItems.AWARE_HOPPER,
				YItems.LEVITATION_CHAMBER,
				YItems.SKELETAL_SORTER_LEFT_HANDED,
				YItems.SKELETAL_SORTER_RIGHT_HANDED,
				YItems.REPLICATOR,
				YItems.HIGH_NOTE_BLOCK,
				YItems.LOW_NOTE_BLOCK,
				YItems.BOGGED_NOTE_BLOCK,
				YItems.BOGGED_HIGH_NOTE_BLOCK,
				YItems.BOGGED_LOW_NOTE_BLOCK
			);
		assign(EQUIPMENT,
				YItems.BROOKITE_SWORD,
				YItems.BROOKITE_SHOVEL,
				YItems.BROOKITE_PICKAXE,
				YItems.BROOKITE_AXE,
				YItems.BROOKITE_HOE,
				YItems.EFFECTOR,
				YItems.SHIFTER,
				YItems.BEDROCK_SMASHER,
				YItems.RIFLE,
				YItems.RIFLE_OVERCLOCKED,
				YItems.RIFLE_REINFORCED,
				YItems.SPECTRAL_AXE,
				YItems.SNARE,
				YItems.SHEARS,
				YItems.CLEAVER,
				YItems.REINFORCED_CLEAVER,
				YItems.SUIT_HELMET,
				YItems.SUIT_CHESTPLATE,
				YItems.SUIT_LEGGINGS,
				YItems.SUIT_BOOTS,
				YItems.GOGGLES,
				YItems.PROJECTOR
			);
		assign(RUINED,
				YItems.RUINED_CONTAINER,
				YItems.RUBBLE
			);
	}

	private static void assign(ItemGroup group, Item... items) {
		for (Item i : items) {
			((AccessorItem)i).yttr$setGroup(group);
		}
	}

}
