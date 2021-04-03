package com.unascribed.yttr;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.mixin.AccessorHorseBaseEntity;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.MoreFiles;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class Yttr implements ModInitializer {
	
	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("yttr", "main"))
			.icon(() -> new ItemStack(Yttr.YTTRIUM_INGOT))
		.build();
	public static final ItemGroup SNARE_ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("yttr", "snare"))
			.icon(() -> new ItemStack(Yttr.SNARE))
		.build();
	public static final ItemGroup LAMP_ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("yttr", "lamp"))
			.icon(() -> Yttr.LAMP.getPickStack(null, null, Yttr.LAMP.getDefaultState().with(LampBlock.COLOR, LampColor.CYAN).with(LampBlock.INVERTED, true)))
		.build();
	
	public static final VoidFluid.Flowing FLOWING_VOID = new VoidFluid.Flowing();
	public static final VoidFluid.Still VOID = new VoidFluid.Still();
	
	public static final StatusEffect DELICACENESS = new StatusEffect(StatusEffectType.BENEFICIAL, 0xA68FE0) {};
	
	public static final Map<Identifier, SoundEvent> craftingSounds = Maps.newHashMap();
	
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
	public static final AwareHopperBlock AWARE_HOPPER = new AwareHopperBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final LevitationChamberBlock LEVITATION_CHAMBER = new LevitationChamberBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
			.nonOpaque()
		);
	public static final ChuteBlock CHUTE = new ChuteBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final VoidGeyserBlock VOID_GEYSER = new VoidGeyserBlock(FabricBlockSettings.of(Material.STONE)
			.strength(-1, 9000000)
			.dropsNothing()
		);
	public static final Block BEDROCK_SMASHER = new BedrockSmasherBlock(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES, 3)
			.strength(35, 4000));
	
	public static final Block RUINED_BEDROCK = new Block(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES, 3)
			.strength(75, 9000000)
			.nonOpaque()
		);
	
	public static final Block GLASSY_VOID = new Block(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES)
			.strength(7)
			.nonOpaque()
		) {
		@Override
		public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
			return world.getMaxLightLevel();
		}
	};
	
	public static final Block SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.breakByTool(FabricToolTags.HOES)
			.breakByTool(FabricToolTags.AXES)
			.strength(2)
		);
	public static final Block STRIPPED_SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.copyOf(SQUEEZE_LOG));
	public static final Block SQUEEZE_LEAVES = new SqueezeLeavesBlock(FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.breakByTool(FabricToolTags.HOES)
			.breakByTool(FabricToolTags.SHEARS)
			.strength(0.2f)
			.suffocates((bs, bv, pos) -> false)
			.blockVision((bs, bv, pos) -> false)
			.nonOpaque()
			.ticksRandomly()
		);
	public static final Block SQUEEZED_LEAVES = new SqueezedLeavesBlock(FabricBlockSettings.copyOf(SQUEEZE_LEAVES)
			.dropsLike(SQUEEZE_LEAVES)
			.dynamicBounds()
		);
	public static final Block SQUEEZE_SAPLING = new SqueezeSaplingBlock(new SqueezeSaplingGenerator(), FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.nonOpaque()
		);
	public static final Block DELICACE_BLOCK = new DelicaceBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
	public static final Block LAMP = new LampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES)
		);
	public static final Block FIXTURE = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 12, 10, 6);
	public static final Block CAGE_LAMP = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 10, 6, 10);
	public static final Block YTTRIUM_PLATING = new Block(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	
	public static final BlockEntityType<AwareHopperBlockEntity> AWARE_HOPPER_ENTITY = new BlockEntityType<>(AwareHopperBlockEntity::new, ImmutableSet.of(AWARE_HOPPER), null);
	public static final BlockEntityType<PowerMeterBlockEntity> POWER_METER_ENTITY = new BlockEntityType<>(PowerMeterBlockEntity::new, ImmutableSet.of(POWER_METER), null);
	public static final BlockEntityType<LevitationChamberBlockEntity> LEVITATION_CHAMBER_ENTITY = new BlockEntityType<>(LevitationChamberBlockEntity::new, ImmutableSet.of(LEVITATION_CHAMBER), null);
	public static final BlockEntityType<ChuteBlockEntity> CHUTE_ENTITY = new BlockEntityType<>(ChuteBlockEntity::new, ImmutableSet.of(CHUTE), null);
	public static final BlockEntityType<VoidGeyserBlockEntity> VOID_GEYSER_ENTITY = new BlockEntityType<>(VoidGeyserBlockEntity::new, ImmutableSet.of(VOID_GEYSER), null);
	public static final BlockEntityType<SqueezedLeavesBlockEntity> SQUEEZED_LEAVES_ENTITY = new BlockEntityType<>(SqueezedLeavesBlockEntity::new, ImmutableSet.of(SQUEEZED_LEAVES), null);
	public static final BlockEntityType<LampBlockEntity> LAMP_ENTITY = new BlockEntityType<>(LampBlockEntity::new, ImmutableSet.of(LAMP, FIXTURE, CAGE_LAMP), null);
	
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
			.group(ITEM_GROUP), 1, 1, false);
	public static final RifleItem RIFLE_REINFORCED = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(ITEM_GROUP), 0.85f, 1, true);
	public static final RifleItem RIFLE_OVERCLOCKED = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(ITEM_GROUP), 1.65f, 2, false);
	public static final SnareItem SNARE = new SnareItem(new Item.Settings()
			.maxDamage(40960)
			.group(ITEM_GROUP)
		);
	public static final ShearsItem SHEARS = new ShearsItem(new Item.Settings()
			.maxDamage(512)
			.group(ITEM_GROUP)
		);
	public static final Item BEDROCK_SHARD = new Item(new Item.Settings()
			.group(ITEM_GROUP)
		);
	public static final Item DELICACE = new SwallowableItem(new Item.Settings()
			.group(ITEM_GROUP)
			.food(new FoodComponent.Builder()
					.alwaysEdible()
					.hunger(1)
					.statusEffect(new StatusEffectInstance(DELICACENESS, 30*20, 3), 1)
					.snack()
					.build())
		) {
		@Override
		public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
			super.appendTooltip(stack, world, tooltip, context);
			tooltip.add(new TranslatableText("potion.withDuration",
					new TranslatableText("potion.withAmplifier",
							new TranslatableText("effect.yttr.delicaceness"),
							new TranslatableText("potion.potency.3")),
					"0:30").formatted(Formatting.BLUE));
			tooltip.add(new LiteralText(""));
			tooltip.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
			tooltip.add(new TranslatableText("tip.yttr.delicace_bonus_1").formatted(Formatting.BLUE));
			tooltip.add(new TranslatableText("tip.yttr.delicace_bonus_2").formatted(Formatting.BLUE));
			tooltip.add(new TranslatableText("tip.yttr.delicace_bonus_3").formatted(Formatting.BLUE));
			tooltip.add(new TranslatableText("tip.yttr.delicace_bonus_4").formatted(Formatting.BLUE));
		}
	};
	public static final Item GLOWING_GAS = new Item(new Item.Settings()
			.group(ITEM_GROUP)
			.maxCount(16)
			.recipeRemainder(Items.GLASS_BOTTLE)
		);
	
	public static final SoundEvent RIFLE_CHARGE = new SoundEvent(new Identifier("yttr", "rifle_charge"));
	public static final SoundEvent RIFLE_CHARGE_FAST = new SoundEvent(new Identifier("yttr", "rifle_charge_fast"));
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
	public static final SoundEvent CRAFT_AWARE_HOPPER = new SoundEvent(new Identifier("yttr", "craft_aware_hopper"));
	public static final SoundEvent AWARE_HOPPER_AMBIENT = new SoundEvent(new Identifier("yttr", "aware_hopper_ambient"));
	public static final SoundEvent AWARE_HOPPER_BREAK = new SoundEvent(new Identifier("yttr", "aware_hopper_break"));
	public static final SoundEvent AWARE_HOPPER_SCREAM = new SoundEvent(new Identifier("yttr", "aware_hopper_scream"));
	public static final SoundEvent SWALLOW = new SoundEvent(new Identifier("yttr", "swallow"));
	
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
	public static final Tag<Block> SNAREABLE_BLOCKS = TagRegistry.block(new Identifier("yttr", "snareable"));
	public static final Tag<Block> UNSNAREABLE_BLOCKS = TagRegistry.block(new Identifier("yttr", "unsnareable"));
	public static final Tag<Fluid> VOID_TAG = AccessorFluidTags.getRequiredTags().add("yttr:void");
	public static final Tag<EntityType<?>> UNSNAREABLE_ENTITY_TAG = TagRegistry.entityType(new Identifier("yttr", "unsnareable"));
	public static final Tag<EntityType<?>> SNAREABLE_NONLIVING_TAG = TagRegistry.entityType(new Identifier("yttr", "snareable_nonliving"));
	public static final Tag<EntityType<?>> BOSSES_TAG = TagRegistry.entityType(new Identifier("yttr", "bosses"));
	public static final Tag<Item> UNSNAREABLE_ITEM_TAG = TagRegistry.item(new Identifier("yttr", "unsnareable"));
	public static final Tag<Item> VOID_IMMUNE_TAG = TagRegistry.item(new Identifier("yttr", "void_immune"));
	public static final Tag<Item> CANNOT_UNBREAKABLE_TAG = TagRegistry.item(new Identifier("yttr", "cannot_unbreakable"));
	
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, "yttr:gadolinite", GADOLINITE);
		Registry.register(Registry.BLOCK, "yttr:yttrium_block", YTTRIUM_BLOCK);
		Registry.register(Registry.BLOCK, "yttr:power_meter", POWER_METER);
		Registry.register(Registry.BLOCK, "yttr:void", VOID_BLOCK);
		Registry.register(Registry.BLOCK, "yttr:aware_hopper", AWARE_HOPPER);
		Registry.register(Registry.BLOCK, "yttr:levitation_chamber", LEVITATION_CHAMBER);
		Registry.register(Registry.BLOCK, "yttr:chute", CHUTE);
		Registry.register(Registry.BLOCK, "yttr:void_geyser", VOID_GEYSER);
		Registry.register(Registry.BLOCK, "yttr:bedrock_smasher", BEDROCK_SMASHER);
		Registry.register(Registry.BLOCK, "yttr:ruined_bedrock", RUINED_BEDROCK);
		Registry.register(Registry.BLOCK, "yttr:glassy_void", GLASSY_VOID);
		Registry.register(Registry.BLOCK, "yttr:squeeze_log", SQUEEZE_LOG);
		Registry.register(Registry.BLOCK, "yttr:stripped_squeeze_log", STRIPPED_SQUEEZE_LOG);
		Registry.register(Registry.BLOCK, "yttr:squeeze_leaves", SQUEEZE_LEAVES);
		Registry.register(Registry.BLOCK, "yttr:squeezed_leaves", SQUEEZED_LEAVES);
		Registry.register(Registry.BLOCK, "yttr:squeeze_sapling", SQUEEZE_SAPLING);
		Registry.register(Registry.BLOCK, "yttr:delicace", DELICACE_BLOCK);
		Registry.register(Registry.BLOCK, "yttr:lamp", LAMP);
		Registry.register(Registry.BLOCK, "yttr:fixture", FIXTURE);
		Registry.register(Registry.BLOCK, "yttr:cage_lamp", CAGE_LAMP);
		Registry.register(Registry.BLOCK, "yttr:yttrium_plating", YTTRIUM_PLATING);
		
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:power_meter", POWER_METER_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:aware_hopper", AWARE_HOPPER_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:levitation_chamber", LEVITATION_CHAMBER_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:chute", CHUTE_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:void_geyser", VOID_GEYSER_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:squeezed_leaves", SQUEEZED_LEAVES_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "yttr:lamp", LAMP_ENTITY);
		
		Registry.register(Registry.ITEM, "yttr:gadolinite", new BlockItem(GADOLINITE, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:yttrium_block", new BlockItem(YTTRIUM_BLOCK, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:power_meter", new BlockItem(POWER_METER, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:aware_hopper", new BlockItem(AWARE_HOPPER, new Item.Settings().group(ITEM_GROUP).maxCount(1)));
		Registry.register(Registry.ITEM, "yttr:levitation_chamber", new LevitationChamberItem(LEVITATION_CHAMBER, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:chute", new BlockItem(CHUTE, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:bedrock_smasher", new BlockItem(BEDROCK_SMASHER, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:glassy_void", new BlockItem(GLASSY_VOID, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:squeeze_log", new BlockItem(SQUEEZE_LOG, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:stripped_squeeze_log", new BlockItem(STRIPPED_SQUEEZE_LOG, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:squeeze_leaves", new BlockItem(SQUEEZE_LEAVES, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:squeeze_sapling", new BlockItem(SQUEEZE_SAPLING, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:lamp", new LampBlockItem(LAMP, new Item.Settings().group(LAMP_ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:fixture", new LampBlockItem(FIXTURE, new Item.Settings().group(LAMP_ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:cage_lamp", new LampBlockItem(CAGE_LAMP, new Item.Settings().group(LAMP_ITEM_GROUP)));
		Registry.register(Registry.ITEM, "yttr:yttrium_plating", new BlockItem(YTTRIUM_PLATING, new Item.Settings().group(ITEM_GROUP)));
		
		Registry.register(Registry.ITEM, "yttr:yttrium_ingot", YTTRIUM_INGOT);
		Registry.register(Registry.ITEM, "yttr:yttrium_nugget", YTTRIUM_NUGGET);
		Registry.register(Registry.ITEM, "yttr:xl_iron_ingot", XL_IRON_INGOT);
		Registry.register(Registry.ITEM, "yttr:rifle", RIFLE);
		Registry.register(Registry.ITEM, "yttr:rifle_reinforced", RIFLE_REINFORCED);
		Registry.register(Registry.ITEM, "yttr:rifle_overclocked", RIFLE_OVERCLOCKED);
		Registry.register(Registry.ITEM, "yttr:void_bucket", VOID_BUCKET);
		Registry.register(Registry.ITEM, "yttr:snare", SNARE);
		Registry.register(Registry.ITEM, "yttr:shears", SHEARS);
		Registry.register(Registry.ITEM, "yttr:bedrock_shard", BEDROCK_SHARD);
		Registry.register(Registry.ITEM, "yttr:delicace", DELICACE);
		Registry.register(Registry.ITEM, "yttr:glowing_gas", GLOWING_GAS);
		
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge", RIFLE_CHARGE);
		Registry.register(Registry.SOUND_EVENT, "yttr:rifle_charge_fast", RIFLE_CHARGE_FAST);
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
		Registry.register(Registry.SOUND_EVENT, "yttr:craft_aware_hopper", CRAFT_AWARE_HOPPER);
		Registry.register(Registry.SOUND_EVENT, "yttr:aware_hopper_ambient", AWARE_HOPPER_AMBIENT);
		Registry.register(Registry.SOUND_EVENT, "yttr:aware_hopper_break", AWARE_HOPPER_BREAK);
		Registry.register(Registry.SOUND_EVENT, "yttr:aware_hopper_scream", AWARE_HOPPER_SCREAM);
		Registry.register(Registry.SOUND_EVENT, "yttr:swallow", SWALLOW);
		
		Registry.register(Registry.FLUID, "yttr:void", VOID);
		Registry.register(Registry.FLUID, "yttr:flowing_void", FLOWING_VOID);
		
		Registry.register(Registry.STATUS_EFFECT, "yttr:delicaceness", DELICACENESS);
		
		Registry.register(Registry.RECIPE_SERIALIZER, "yttr:lamp_crafting", new ShapedRecipe.Serializer() {
			@Override
			public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
				return new LampRecipe(super.read(identifier, jsonObject));
			}
			
			@Override
			public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
				return new LampRecipe(super.read(identifier, packetByteBuf));
			}
		});
		
		RegistryKey<ConfiguredFeature<?, ?>> gadoliniteOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("yttr", "gadolinite_overworld"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, gadoliniteOverworld.getValue(), GADOLINITE_OVERWORLD);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, gadoliniteOverworld);
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "rifle_mode"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player.getMainHandStack().getItem() instanceof RifleItem) {
				((RifleItem)player.getMainHandStack().getItem()).attack(player);
			}
		});
		
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			// TODO pick random chunks
			for (BlockEntity be : ImmutableList.copyOf(world.blockEntities)) {
				if (be instanceof Inventory && world.random.nextInt(40) == 0) {
					Inventory inv = (Inventory)be;
					for (int i = 0; i < inv.size(); i++) {
						ItemStack is = inv.getStack(i);
						if (is.getItem() == SNARE) {
							SNARE.blockInventoryTick(is, world, be.getPos(), i);
							inv.setStack(i, is);
						}
					}
				}
			}
			for (Entity e : world.getEntitiesByType(null, Predicates.alwaysTrue())) {
				if (e instanceof PlayerEntity) {
					EnderChestInventory inv = ((PlayerEntity) e).getEnderChestInventory();
					for (int i = 0; i < inv.size(); i++) {
						ItemStack is = inv.getStack(i);
						if (is.getItem() == SNARE) {
							SNARE.inventoryTick(is, world, e, i, false);
							inv.setStack(i, is);
						}
					}
					continue;
				}
				if (e instanceof ItemEntity) {
					ItemStack is = ((ItemEntity) e).getStack();
					if (is.getItem() == SNARE) {
						SNARE.inventoryTick(is, world, e, 0, false);
						if (is.isEmpty()) e.remove();
					}
					continue;
				}
				if (e instanceof ItemFrameEntity) {
					ItemStack is = ((ItemFrameEntity) e).getHeldItemStack();
					if (is.getItem() == SNARE) {
						SNARE.inventoryTick(is, world, e, 0, false);
						if (is.isEmpty()) {
							((ItemFrameEntity) e).setHeldItemStack(ItemStack.EMPTY, true);
						}
					}
					continue;
				}
				if (world.random.nextInt(40) == 0) {
					Set<ItemStack> seen = Sets.newIdentityHashSet();
					if (e instanceof HorseBaseEntity) {
						SimpleInventory inv = ((AccessorHorseBaseEntity)e).yttr$getItems();
						for (int i = 0; i < inv.size(); i++) {
							ItemStack is = inv.getStack(i);
							if (is.getItem() == SNARE && seen.add(is)) {
								SNARE.inventoryTick(is, world, e, i, false);
								inv.setStack(i, is);
							}
						}
					}
					if (e instanceof LivingEntity) {
						for (EquipmentSlot slot : EquipmentSlot.values()) {
							ItemStack is = ((LivingEntity) e).getEquippedStack(slot);
							if (is.getItem() == SNARE && seen.add(is)) {
								SNARE.inventoryTick(is, world, e, slot.getEntitySlotId(), false);
								e.equipStack(slot, is);
							}
						}
					}
					if (e instanceof Inventory) {
						Inventory inv = (Inventory)e;
						for (int i = 0; i < inv.size(); i++) {
							ItemStack is = inv.getStack(i);
							if (is.getItem() == SNARE && seen.add(is)) {
								SNARE.inventoryTick(is, world, e, i, false);
								inv.setStack(i, is);
							}
						}
					}
				}
			}
		});
		
		CommandRegistrationCallback.EVENT.register((dispatcher, dedi) -> {
			dispatcher.register(CommandManager.literal("yttr:void_undo")
					.requires((scs) -> scs.hasPermissionLevel(4))
					.then(CommandManager.literal("clean")
						.executes((ctx) -> {
							try {
								Path dir = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
								long count;
								if (Files.exists(dir)) {
									count = Files.list(dir).count();
									MoreFiles.deleteRecursively(dir);
								} else {
									count = 0;
								}
								ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.clean", count), true);
							} catch (IOException e) {
								LogManager.getLogger("Yttr").warn("Failed to clean undos", e);
								throw new UncheckedIOException(e);
							}
							return 1;
						})
					)
					.then(CommandManager.literal("just")
						.then(CommandManager.argument("file", StringArgumentType.greedyString())
							.suggests((ctx, bldr) -> {
								ServerCommandSource scs = ctx.getSource();
								Path root = VoidLogic.getUndoDirectory(scs.getMinecraftServer());
								String dim = scs.getWorld().getRegistryKey().getValue().toString();
								BlockPos pos = new BlockPos(scs.getPosition());
								ChunkPos chunkPos = new ChunkPos(pos);
								return CompletableFuture.supplyAsync(() -> {
									try {
										Path indexFile = root.resolve("index.dat");
										if (Files.exists(indexFile)) {
											CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
											CompoundTag byChunk = index.getCompound("ByChunk");
											List<Pair<Double, Runnable>> suggestorsByDistance = Lists.newArrayList();
											for (int x = -2; x <= 2; x++) {
												for (int z = -2; z <= 2; z++) {
													String s = (chunkPos.x+x)+" "+(chunkPos.z+z);
													if (byChunk.contains(s, NbtType.LIST)) {
														ListTag list = byChunk.getList(s, NbtType.COMPOUND);
														for (int i = 0; i < list.size(); i++) {
															CompoundTag entry = list.getCompound(i);
															if (!entry.getString("Dim").equals(dim)) continue;
															int hpos = entry.getByte("HPos")&0xFF;
															BlockPos entryPos = new ChunkPos(chunkPos.x+x, chunkPos.z+z).getStartPos().add((hpos>>4)&0xF, entry.getInt("YPos"), hpos&0xF);
															double dist = entryPos.getSquaredDistance(pos);
															if (dist < 32*32) {
																suggestorsByDistance.add(Pair.of(dist, () -> bldr.suggest(entry.getString("Name"), new TranslatableText("commands.yttr.void_undo.location", entryPos.getX(), entryPos.getY(), entryPos.getZ(), (int)MathHelper.sqrt(dist)))));
															}
														}
													}
												}
											}
											Collections.sort(suggestorsByDistance, (a, b) -> Double.compare(a.getFirst(), b.getFirst()));
											for (Pair<Double, Runnable> p : suggestorsByDistance) {
												p.getSecond().run();
											}
										}
										return bldr.build();
									} catch (IOException e) {
										LogManager.getLogger("Yttr").warn("Failed to suggest undos", e);
										throw new UncheckedIOException(e);
									}
								});
							})
							.executes((ctx) -> {
								Path root = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
								Path indexFile = root.resolve("index.dat");
								String fname = StringArgumentType.getString(ctx, "file");
								Path file = root.resolve(fname+".dat");
								if (Files.exists(file)) {
									try {
										CompoundTag data = NbtIo.readCompressed(file.toFile());
										int count = undo(ctx.getSource().getMinecraftServer(), data);
										if (Files.exists(indexFile)) {
											CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
											removeFromIndex(index, Collections.singleton(fname));
											NbtIo.writeCompressed(index, indexFile.toFile());
											Files.delete(file);
										}
										ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.success", count), true);
									} catch (IOException e) {
										LogManager.getLogger("Yttr").warn("Failed to undo", e);
										throw new UncheckedIOException(e);
									}
								} else {
									throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
								}
								return 1;
							})
						)
					)
					.then(CommandManager.literal("by")
						.then(CommandManager.argument("user", StringArgumentType.greedyString())
							.suggests((ctx, bldr) -> {
								ServerCommandSource scs = ctx.getSource();
								Path root = VoidLogic.getUndoDirectory(scs.getMinecraftServer());
								return CompletableFuture.supplyAsync(() -> {
									try {
										Path indexFile = root.resolve("index.dat");
										if (Files.exists(indexFile)) {
											CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
											CompoundTag byUser = index.getCompound("ByUser");
											for (String k : byUser.getKeys()) {
												CompoundTag tag = byUser.getCompound(k);
												bldr.suggest(tag.getString("Username"), new LiteralText(k));
											}
										}
										return bldr.build();
									} catch (IOException e) {
										LogManager.getLogger("Yttr").warn("Failed to suggest undos", e);
										throw new UncheckedIOException(e);
									}
								});
							})
							.executes((ctx) -> {
								Path root = VoidLogic.getUndoDirectory(ctx.getSource().getMinecraftServer());
								Path indexFile = root.resolve("index.dat");
								String user = StringArgumentType.getString(ctx, "user");
								Set<String> fnames = Sets.newHashSet();
								try {
									if (Files.exists(indexFile)) {
										CompoundTag index = NbtIo.readCompressed(indexFile.toFile());
										CompoundTag byUser = index.getCompound("ByUser");
										for (String k : byUser.getKeys()) {
											CompoundTag tag = byUser.getCompound(k);
											if (k.equals(user) || tag.getString("Username").equals(user)) {
												ListTag list = tag.getList("List", NbtType.STRING);
												for (int i = 0; i < list.size(); i++) {
													fnames.add(list.getString(i));
												}
											}
										}
										if (!fnames.isEmpty()) {
											int count = 0;
											int success = 0;
											for (String fname : fnames) {
												try {
													Path file = root.resolve(fname+".dat");
													CompoundTag data = NbtIo.readCompressed(file.toFile());
													count += undo(ctx.getSource().getMinecraftServer(), data);
													success++;
													if (Files.exists(indexFile)) {
														Files.delete(file);
													}
												} catch (IOException e) {
													LogManager.getLogger("Yttr").warn("Failed to undo "+fname, e);
												}
											}
											ctx.getSource().sendFeedback(new TranslatableText("commands.yttr.void_undo.success_multi", count, success), true);
											removeFromIndex(index, fnames);
											NbtIo.writeCompressed(index, indexFile.toFile());
										} else {
											throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
										}
									} else {
										throw new CommandException(new TranslatableText("commands.yttr.void_undo.not_found"));
									}
								} catch (IOException e) {
									LogManager.getLogger("Yttr").warn("Failed to read/update index", e);
									throw new UncheckedIOException(e);
								}
								return 1;
							})
						)
					)
				);
		});
	}

	private int undo(MinecraftServer server, CompoundTag data) {
		int count = 0;
		Identifier dim = new Identifier(data.getString("Dim"));
		World world = server.getWorld(RegistryKey.of(Registry.DIMENSION, dim));
		if (world == null) {
			throw new CommandException(new TranslatableText("commands.yttr.void_undo.no_world", dim.toString()));
		}
		int[] posArr = data.getIntArray("Pos");
		BlockPos pos = new BlockPos(posArr[0], posArr[1], posArr[2]);
		ListTag blocks = data.getList("Blocks", NbtType.COMPOUND);
		BlockPos.Mutable mut = new BlockPos.Mutable();
		for (int i = 0; i < blocks.size(); i++) {
			CompoundTag block = blocks.getCompound(i);
			byte[] posOfs = block.getByteArray("Pos");
			mut.set(pos.getX()+posOfs[0], pos.getY()+posOfs[1], pos.getZ()+posOfs[2]);
			Block b = Registry.BLOCK.get(new Identifier(block.getString("Block")));
			if (b == null) continue;
			BlockState bs = b.getDefaultState();
			if (block.contains("State", NbtType.COMPOUND)) {
				CompoundTag state = block.getCompound("State");
				for (Property<?> prop : bs.getProperties()) {
					if (state.contains(prop.getName(), NbtType.STRING)) {
						bs = setParseProperty(bs, prop, state.getString(prop.getName()));
					}
				}
			}
			if (bs.isAir() && !world.isAir(mut)) continue;
			if (world.setBlockState(mut, bs)) count++;
			if (block.contains("Entity", NbtType.COMPOUND)) {
				CompoundTag tag = block.getCompound("Entity");
				if (world.getBlockEntity(mut) != null) {
					world.getBlockEntity(mut).fromTag(bs, tag);
				} else {
					world.setBlockEntity(mut, BlockEntity.createFromTag(bs, tag));
				}
			}
		}
		return count;
	}

	private void removeFromIndex(CompoundTag index, Set<String> fnames) {
		CompoundTag byChunk = index.getCompound("ByChunk");
		for (String k : ImmutableList.copyOf(byChunk.getKeys())) {
			ListTag list = byChunk.getList(k, NbtType.COMPOUND);
			for (int i = list.size()-1; i >= 0; i--) {
				if (fnames.contains(list.getCompound(i).getString("Name"))) {
					list.remove(i);
				}
			}
			if (list.isEmpty()) {
				byChunk.remove(k);
			}
		}
		CompoundTag byUser = index.getCompound("ByUser");
		for (String k : ImmutableList.copyOf(byUser.getKeys())) {
			CompoundTag userData = byUser.getCompound(k);
			ListTag list = userData.getList("List", NbtType.STRING);
			for (int i = list.size()-1; i >= 0; i--) {
				if (fnames.contains(list.getString(i))) {
					list.remove(i);
				}
			}
			if (list.isEmpty()) {
				byUser.remove(k);
			}
		}
	}

	private <T extends Comparable<T>> BlockState setParseProperty(BlockState bs, Property<T> prop, String val) {
		Optional<T> opt = prop.parse(val);
		if (!opt.isPresent()) return bs;
		return bs.with(prop, opt.get());
	}
	
}
