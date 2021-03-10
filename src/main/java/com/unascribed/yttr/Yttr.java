package com.unascribed.yttr;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class Yttr implements ModInitializer {
	
	public static ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("yttr", "main"))
			.icon(() -> new ItemStack(Registry.ITEM.get(new Identifier("yttr", "yttrium_ingot"))))
		.build();
	
	public static final VoidFluid.Flowing FLOWING_VOID = new VoidFluid.Flowing();
	public static final VoidFluid.Still VOID = new VoidFluid.Still();
	
	public static final Block GADOLINITE = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final Block YTTRIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final PowerMeterBlock POWER_METER = new PowerMeterBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final VoidFluidBlock VOID_BLOCK = new VoidFluidBlock(VOID, FabricBlockSettings.of(
				new FabricMaterialBuilder(MaterialColor.WATER)
					.allowsMovement()
					.lightPassesThrough()
					.notSolid()
					.destroyedByPiston()
					.replaceable()
					.liquid()
					.build()
				)
			.noCollision()
			.strength(100)
			.dropsNothing()
		);
	
	public static final BlockEntityType<PowerMeterBlockEntity> POWER_METER_ENTITY = new BlockEntityType<>(PowerMeterBlockEntity::new, ImmutableSet.of(POWER_METER), null);
	
	public static final Item YTTRIUM_INGOT = new Item(new Item.Settings()
			.group(ITEM_GROUP)
		);
	public static final Item YTTRIUM_NUGGET = new Item(new Item.Settings()
			.group(ITEM_GROUP)
		);
	public static final Item XL_IRON_INGOT = new Item(new Item.Settings()
			.maxCount(16)
			.group(ITEM_GROUP)
		);
	public static final BucketItem VOID_BUCKET = new BucketItem(VOID, new Item.Settings()
			.recipeRemainder(Items.BUCKET)
			.maxCount(1)
			.group(ITEM_GROUP)
		);
	public static final RifleItem RIFLE = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(ITEM_GROUP)
		);
	
	public static final SoundEvent RIFLE_CHARGE = new SoundEvent(new Identifier("yttr", "rifle_charge"));
	public static final SoundEvent RIFLE_CHARGE_CONTINUE = new SoundEvent(new Identifier("yttr", "rifle_charge_continue"));
	public static final SoundEvent RIFLE_CHARGE_RATTLE = new SoundEvent(new Identifier("yttr", "rifle_charge_rattle"));
	public static final SoundEvent RIFLE_CHARGE_CANCEL = new SoundEvent(new Identifier("yttr", "rifle_charge_cancel"));
	public static final SoundEvent RIFLE_FIRE = new SoundEvent(new Identifier("yttr", "rifle_fire"));
	public static final SoundEvent RIFLE_FIRE_DUD = new SoundEvent(new Identifier("yttr", "rifle_fire_dud"));
	public static final SoundEvent RIFLE_OVERCHARGE = new SoundEvent(new Identifier("yttr", "rifle_overcharge"));
	public static final SoundEvent RIFLE_VENT = new SoundEvent(new Identifier("yttr", "rifle_vent"));
	public static final SoundEvent RIFLE_LOAD = new SoundEvent(new Identifier("yttr", "rifle_load"));
	public static final SoundEvent RIFLE_WASTE = new SoundEvent(new Identifier("yttr", "rifle_waste"));
	public static final SoundEvent VOID_SOUND = new SoundEvent(new Identifier("yttr", "void"));
	public static final SoundEvent DISSOLVE = new SoundEvent(new Identifier("yttr", "dissolve"));
	
	private static final ConfiguredFeature<?, ?> GADOLINITE_OVERWORLD = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					GADOLINITE.getDefaultState(),
					9))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(
					20,
					0,
					96)))
			.spreadHorizontally()
			.repeat(8);
	
	public static final Tag<Block> FIRE_MODE_INSTABREAK = TagRegistry.block(new Identifier("yttr", "fire_mode_instabreak"));
	public static final Tag<Fluid> VOID_TAG = AccessorFluidTags.getRequiredTags().add("yttr:void");
	
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, "yttr:gadolinite", GADOLINITE);
		Registry.register(Registry.BLOCK, "yttr:yttrium_block", YTTRIUM_BLOCK);
		Registry.register(Registry.BLOCK, "yttr:power_meter", POWER_METER);
		Registry.register(Registry.BLOCK, "yttr:void", VOID_BLOCK);
		
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:power_meter", POWER_METER_ENTITY);
		
		Registry.register(Registry.ITEM, "yttr:gadolinite", new BlockItem(GADOLINITE, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:yttrium_block", new BlockItem(YTTRIUM_BLOCK, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:power_meter", new BlockItem(POWER_METER, new Item.Settings().group(ITEM_GROUP)));
		
		Registry.register(Registry.ITEM, "yttr:yttrium_ingot", YTTRIUM_INGOT);
		Registry.register(Registry.ITEM, "yttr:yttrium_nugget", YTTRIUM_NUGGET);
		Registry.register(Registry.ITEM, "yttr:xl_iron_ingot", XL_IRON_INGOT);
		Registry.register(Registry.ITEM, "yttr:rifle", RIFLE);
		Registry.register(Registry.ITEM, "yttr:void_bucket", VOID_BUCKET);
		
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge", RIFLE_CHARGE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge_continue", RIFLE_CHARGE_CONTINUE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge_rattle", RIFLE_CHARGE_RATTLE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge_cancel", RIFLE_CHARGE_CANCEL);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_fire", RIFLE_FIRE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_fire_dud", RIFLE_FIRE_DUD);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_overcharge", RIFLE_OVERCHARGE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_vent", RIFLE_VENT);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_load", RIFLE_LOAD);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_waste", RIFLE_WASTE);
		Registry.register(Registry.SOUND_EVENT, "yttr:void", VOID_SOUND);
		Registry.register(Registry.SOUND_EVENT, "yttr:dissolve", DISSOLVE);
		
		Registry.register(Registry.FLUID, "yttr:void", VOID);
		Registry.register(Registry.FLUID, "yttr:flowing_void", FLOWING_VOID);
		
		RegistryKey<ConfiguredFeature<?, ?>> gadoliniteOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("yttr", "gadolinite_overworld"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, gadoliniteOverworld.getValue(), GADOLINITE_OVERWORLD);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), net.minecraft.world.gen.GenerationStep.Feature.UNDERGROUND_ORES, gadoliniteOverworld);
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "rifle_mode"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player.getMainHandStack().getItem() == RIFLE) {
				RIFLE.attack(player);
			}
		});
	}
	
}
