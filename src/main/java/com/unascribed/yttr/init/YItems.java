package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.AmmoCanItem;
import com.unascribed.yttr.content.item.BlueCubeItem;
import com.unascribed.yttr.content.item.CleaverItem;
import com.unascribed.yttr.content.item.DropOfContinuityItem;
import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.content.item.ProjectorItem;
import com.unascribed.yttr.content.item.HornItem;
import com.unascribed.yttr.content.item.InRedMultimeterItem;
import com.unascribed.yttr.content.item.ReinforcedCleaverItem;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.content.item.ShearsItem;
import com.unascribed.yttr.content.item.ShifterItem;
import com.unascribed.yttr.content.item.SnareItem;
import com.unascribed.yttr.content.item.SpectralAxeItem;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.content.item.SwallowableItem;
import com.unascribed.yttr.content.item.VoidBucketItem;
import com.unascribed.yttr.content.item.block.BigBlockItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.content.item.block.LevitationChamberBlockItem;
import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.content.item.block.SkeletalSorterBlockItem;
import com.unascribed.yttr.content.item.potion.MercurialPotionItem;
import com.unascribed.yttr.content.item.potion.MercurialSplashPotionItem;
import com.unascribed.yttr.util.LatchReference;
import com.unascribed.yttr.util.annotate.ConstantColor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class YItems {

	public static final BlockItem GADOLINITE = createNormalBlockItem(YBlocks.GADOLINITE);
	public static final BlockItem YTTRIUM_BLOCK = createNormalBlockItem(YBlocks.YTTRIUM_BLOCK);
	public static final BlockItem POWER_METER = createNormalBlockItem(YBlocks.POWER_METER);
	public static final BlockItem CHUTE = createNormalBlockItem(YBlocks.CHUTE);
	public static final BlockItem BEDROCK_SMASHER = createNormalBlockItem(YBlocks.BEDROCK_SMASHER);
	public static final BlockItem GLASSY_VOID = createNormalBlockItem(YBlocks.GLASSY_VOID);
	public static final BlockItem GLASSY_VOID_PANE = createNormalBlockItem(YBlocks.GLASSY_VOID_PANE);
	public static final BlockItem SQUEEZE_LOG = createNormalBlockItem(YBlocks.SQUEEZE_LOG);
	public static final BlockItem STRIPPED_SQUEEZE_LOG = createNormalBlockItem(YBlocks.STRIPPED_SQUEEZE_LOG);
	@ConstantColor(0xFFEE58)
	public static final BlockItem SQUEEZE_LEAVES = createNormalBlockItem(YBlocks.SQUEEZE_LEAVES);
	public static final BlockItem SQUEEZE_SAPLING = createNormalBlockItem(YBlocks.SQUEEZE_SAPLING);
	public static final BlockItem YTTRIUM_PLATING = createNormalBlockItem(YBlocks.YTTRIUM_PLATING);
	public static final BlockItem LIGHT_YTTRIUM_PLATE = createNormalBlockItem(YBlocks.LIGHT_YTTRIUM_PLATE);
	public static final BlockItem HEAVY_YTTRIUM_PLATE = createNormalBlockItem(YBlocks.HEAVY_YTTRIUM_PLATE);
	public static final BlockItem CENTRIFUGE = createNormalBlockItem(YBlocks.CENTRIFUGE);
	public static final BlockItem DOPPER = createNormalBlockItem(YBlocks.DOPPER);
	public static final BlockItem FLOPPER = new HornItem(YBlocks.FLOPPER, new Item.Settings());
	public static final BlockItem DIVING_PLATE = createNormalBlockItem(YBlocks.DIVING_PLATE);
	public static final BlockItem SUIT_STATION = createNormalBlockItem(YBlocks.SUIT_STATION);
	public static final BlockItem TABLE = createNormalBlockItem(YBlocks.TABLE);
	public static final BlockItem ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.ULTRAPURE_CARBON_BLOCK, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final BlockItem COMPRESSED_ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.COMPRESSED_ULTRAPURE_CARBON_BLOCK, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final BlockItem ENCASED_VOID_FILTER = createNormalBlockItem(YBlocks.ENCASED_VOID_FILTER);
	public static final BlockItem VOID_FILTER = createNormalBlockItem(YBlocks.VOID_FILTER);
	public static final BlockItem BROOKITE_ORE = createNormalBlockItem(YBlocks.BROOKITE_ORE);
	public static final BlockItem ROOT_OF_CONTINUITY = createNormalBlockItem(YBlocks.ROOT_OF_CONTINUITY);
	public static final BlockItem YTTRIUM_BUTTON = createNormalBlockItem(YBlocks.YTTRIUM_BUTTON);
	public static final BlockItem BROOKITE_BLOCK = createNormalBlockItem(YBlocks.BROOKITE_BLOCK);
	public static final BlockItem NETHERTUFF = createNormalBlockItem(YBlocks.NETHERTUFF);
	public static final BlockItem MAGTUBE = createNormalBlockItem(YBlocks.MAGTUBE);
	public static final BlockItem HIGH_NOTE_BLOCK = createNormalBlockItem(YBlocks.HIGH_NOTE_BLOCK);
	public static final BlockItem LOW_NOTE_BLOCK = createNormalBlockItem(YBlocks.LOW_NOTE_BLOCK);
	public static final BlockItem BOGGED_NOTE_BLOCK = createNormalBlockItem(YBlocks.BOGGED_NOTE_BLOCK);
	public static final BlockItem BOGGED_HIGH_NOTE_BLOCK = createNormalBlockItem(YBlocks.BOGGED_HIGH_NOTE_BLOCK);
	public static final BlockItem BOGGED_LOW_NOTE_BLOCK = createNormalBlockItem(YBlocks.BOGGED_LOW_NOTE_BLOCK);
	@ConstantColor(0xCB8FC3)
	public static final BlockItem CONTINUOUS_PLATFORM = createNormalBlockItem(YBlocks.CONTINUOUS_PLATFORM);
	public static final BlockItem CLAMBER_BLOCK = createNormalBlockItem(YBlocks.CLAMBER_BLOCK);
	public static final BlockItem SOUL_CLAMBER_BLOCK = createNormalBlockItem(YBlocks.SOUL_CLAMBER_BLOCK);
	public static final BlockItem SOUL_PLANKS = createNormalBlockItem(YBlocks.SOUL_PLANKS);
	public static final BlockItem CUPROSTEEL_BLOCK = createNormalBlockItem(YBlocks.CUPROSTEEL_BLOCK);
	public static final BlockItem CUPROSTEEL_PLATE = createNormalBlockItem(YBlocks.CUPROSTEEL_PLATE);
	public static final BlockItem CAN_FILLER = createNormalBlockItem(YBlocks.CAN_FILLER);
	public static final BlockItem DUST = createNormalBlockItem(YBlocks.DUST);

	public static final BlockItem INRED_BLOCK = createNormalBlockItem(YBlocks.INRED_BLOCK);
	public static final BlockItem INRED_CABLE = createNormalBlockItem(YBlocks.INRED_CABLE);
	public static final BlockItem INRED_SCAFFOLD = createNormalBlockItem(YBlocks.INRED_SCAFFOLD);
	public static final BlockItem INRED_AND_GATE = createNormalBlockItem(YBlocks.INRED_AND_GATE);
	public static final BlockItem INRED_NOT_GATE = createNormalBlockItem(YBlocks.INRED_NOT_GATE);
	public static final BlockItem INRED_XOR_GATE = createNormalBlockItem(YBlocks.INRED_XOR_GATE);
	public static final BlockItem INRED_DIODE = createNormalBlockItem(YBlocks.INRED_DIODE);
	public static final BlockItem INRED_SHIFTER = createNormalBlockItem(YBlocks.INRED_SHIFTER);
	public static final BlockItem INRED_TRANSISTOR = createNormalBlockItem(YBlocks.INRED_TRANSISTOR);
	public static final BlockItem INRED_ENCODER = createNormalBlockItem(YBlocks.INRED_ENCODER);
	public static final BlockItem INRED_OSCILLATOR = createNormalBlockItem(YBlocks.INRED_OSCILLATOR);
	public static final BlockItem INRED_DEMO_CYCLER = createNormalBlockItem(YBlocks.INRED_DEMO_CYCLER);
	
	public static final BlockItem WASTELAND_DIRT = createRuinedBlockItem(YBlocks.WASTELAND_DIRT);
	public static final BlockItem WASTELAND_GRASS = createRuinedBlockItem(YBlocks.WASTELAND_GRASS);
	public static final BlockItem WASTELAND_LOG = createRuinedBlockItem(YBlocks.WASTELAND_LOG);
	public static final BlockItem WASTELAND_STONE = createRuinedBlockItem(YBlocks.WASTELAND_STONE);
	
	public static final BlockItem RUINED_COBBLESTONE = createRuinedBlockItem(YBlocks.RUINED_COBBLESTONE);
	public static final BlockItem RUINED_BRICKS = createRuinedBlockItem(YBlocks.RUINED_BRICKS);
	public static final BlockItem RUINED_CONTAINER = createRuinedBlockItem(YBlocks.RUINED_CONTAINER);
	public static final BlockItem RUINED_DEVICE_BC_1 = createRuinedBlockItem(YBlocks.RUINED_DEVICE_BC_1);
	public static final BlockItem RUINED_DEVICE_BC_2 = createRuinedBlockItem(YBlocks.RUINED_DEVICE_BC_2);
	public static final BlockItem RUINED_DEVICE_GT_1 = createRuinedBlockItem(YBlocks.RUINED_DEVICE_GT_1);
	public static final BlockItem RUINED_DEVICE_RP_1 = createRuinedBlockItem(YBlocks.RUINED_DEVICE_RP_1);
	public static final BlockItem RUINED_DEVICE_FO_1 = createRuinedBlockItem(YBlocks.RUINED_DEVICE_FO_1);
	public static final BlockItem RUINED_PIPE = createRuinedBlockItem(YBlocks.RUINED_PIPE);
	public static final BlockItem RUINED_FRAME = createRuinedBlockItem(YBlocks.RUINED_FRAME);
	public static final BlockItem RUINED_TUBE = createRuinedBlockItem(YBlocks.RUINED_TUBE);
	public static final BlockItem RUINED_LEVER = createRuinedBlockItem(YBlocks.RUINED_LEVER);
	public static final BlockItem RUINED_TANK = createRuinedBlockItem(YBlocks.RUINED_TANK);
	public static final BlockItem RUINED_CONSTRUCT_RC_1 = createRuinedBlockItem(YBlocks.RUINED_CONSTRUCT_RC_1);
	public static final BlockItem RUINED_CONSTRUCT_RC_2 = createRuinedBlockItem(YBlocks.RUINED_CONSTRUCT_RC_2);
	
	public static final BlockItem RUINED_TORCH = new WallStandingBlockItem(YBlocks.RUINED_TORCH, YBlocks.RUINED_WALL_TORCH, new Item.Settings());
	
	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem LAMP = new LampBlockItem(YBlocks.LAMP, new Item.Settings());

	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem FIXTURE = new LampBlockItem(YBlocks.FIXTURE, new Item.Settings());

	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem CAGE_LAMP = new LampBlockItem(YBlocks.CAGE_LAMP, new Item.Settings());
	
	public static final BlockItem LAZOR_EMITTER = new LampBlockItem(YBlocks.LAZOR_EMITTER, new Item.Settings());
	
	public static final BlockItem AWARE_HOPPER = new BlockItem(YBlocks.AWARE_HOPPER, new Item.Settings()
			.maxCount(1));

	public static final BlockItem LEVITATION_CHAMBER = new LevitationChamberBlockItem(YBlocks.LEVITATION_CHAMBER, new Item.Settings());
	
	public static final LatchReference<BlockItem> COPPER_ORE = YLatches.create();
	
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_RIGHT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.RIGHT, new Item.Settings());
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_LEFT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.LEFT, new Item.Settings());
	
	@BuiltinRenderer("ReplicatorItemRenderer")
	public static final ReplicatorBlockItem REPLICATOR = new ReplicatorBlockItem(YBlocks.REPLICATOR, new Item.Settings());
	
	public static final BigBlockItem MAGTANK = new BigBlockItem(YBlocks.MAGTANK, new Item.Settings()
			.maxCount(4));
	
//	public static final BigBlockItem GIANT_COBBLESTONE = new BigBlockItem(YBlocks.GIANT_COBBLESTONE, new Item.Settings()
//			.maxCount(1));
	
	public static final BigBlockItem DSU = new BigBlockItem(YBlocks.DSU, new Item.Settings()
			.maxCount(8));

	private static BlockItem createNormalBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings());
	}
	
	private static BlockItem createRuinedBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings());
	}

	public static final Item YTTRIUM_INGOT = new Item(new Item.Settings());
	
	public static final Item YTTRIUM_NUGGET = new Item(new Item.Settings());
	
	public static final Item XL_IRON_INGOT = new Item(new Item.Settings()
			.maxCount(16));
	
	public static final VoidBucketItem VOID_BUCKET = new VoidBucketItem(new Item.Settings()
			.recipeRemainder(Items.BUCKET)
			.maxCount(1));
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE = new RifleItem(new Item.Settings()
			.maxCount(1), 1, 1, false, 0x3E5656);
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE_REINFORCED = new RifleItem(new Item.Settings()
			.maxCount(1), 0.85f, 1, true, 0x223333);
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE_OVERCLOCKED = new RifleItem(new Item.Settings()
			.maxCount(1), 1.65f, 2, false, 0x111111);
	
	public static final SnareItem SNARE = new SnareItem(new Item.Settings()
			.maxDamage(40960));
	
	public static final ShearsItem SHEARS = new ShearsItem(new Item.Settings()
			.maxDamage(512));
	
	public static final Item BEDROCK_SHARD = new Item(new Item.Settings());
	
	public static final Item DELICACE = new SwallowableItem(new Item.Settings()
			.food(new FoodComponent.Builder()
					.alwaysEdible()
					.hunger(1)
					.statusEffect(new StatusEffectInstance(YStatusEffects.DELICACENESS, 30*20, 3), 1)
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
			.maxCount(16)
			.recipeRemainder(Items.GLASS_BOTTLE));
	
	public static final Item LOGO = new Item(new Item.Settings()) {
		@Override
		public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {}
	};
	
	public static final Item CLEAVER = new CleaverItem(new Item.Settings()
			.maxDamage(1562));
	
	public static final Item REINFORCED_CLEAVER = new ReinforcedCleaverItem(new Item.Settings()
			.maxDamage(3072)
			.fireproof());
	
	public static final EffectorItem EFFECTOR = new EffectorItem(new Item.Settings()
			.maxCount(1));
	
	public static final Item NEODYMIUM_DUST = new Item(new Item.Settings());
	public static final MusicDiscItem NEODYMIUM_DISC = new MusicDiscItem(15, YSounds.BUZZ, new Item.Settings()) {
		@Override
		@Environment(EnvType.CLIENT)
		public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
			// no-op to remove music disc tooltip
		}
	};
	
	private static final ArmorMaterial SUIT_MATERIAL = new ArmorMaterial() {

		@Override
		public int getDurability(EquipmentSlot slot) {
			return ArmorMaterials.DIAMOND.getDurability(slot);
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return ArmorMaterials.DIAMOND.getProtectionAmount(slot)+2;
		}

		@Override
		public int getEnchantability() {
			return 5;
		}

		@Override
		public SoundEvent getEquipSound() {
			return YSounds.EQUIP_SUIT;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.YTTRIUM_BLOCK);
		}

		@Override
		public String getName() {
			return "yttr_suit";
		}

		@Override
		public float getToughness() {
			return 6;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.5f;
		}
		
	};
	
	@SimpleArmorTexture("yttr:suit")
	public static final SuitArmorItem SUIT_HELMET = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.HEAD, new Item.Settings()
			.fireproof());
	
	@SimpleArmorTexture("yttr:suit")
	public static final SuitArmorItem SUIT_CHESTPLATE = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.CHEST, new Item.Settings()
			.fireproof());
	
	@SimpleArmorTexture("yttr:suit")
	public static final SuitArmorItem SUIT_LEGGINGS = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.LEGS, new Item.Settings()
			.fireproof());
	
	@SimpleArmorTexture("yttr:suit")
	public static final SuitArmorItem SUIT_BOOTS = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.FEET, new Item.Settings()
			.fireproof());
	
	public static final Item ARMOR_PLATING = new Item(new Item.Settings());
	
	@SimpleArmorTexture("yttr:goggles")
	public static final ArmorItem GOGGLES = new ArmorItem(new ArmorMaterial() {
		
		@Override
		public float getToughness() {
			return 0;
		}
		
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.YTTRIUM_NUGGET);
		}
		
		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return 0;
		}
		
		@Override
		public String getName() {
			return "yttr_goggles";
		}
		
		@Override
		public float getKnockbackResistance() {
			return 0;
		}
		
		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}
		
		@Override
		public int getEnchantability() {
			return 0;
		}
		
		@Override
		public int getDurability(EquipmentSlot slot) {
			return 32;
		}
	}, EquipmentSlot.HEAD, new Item.Settings()) {


		@Override
		public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
			return ImmutableMultimap.of();
		}
		
	};
	
	public static final SpectralAxeItem SPECTRAL_AXE = new SpectralAxeItem();
	
	private static final Item.Settings UP_SETTINGS = new Item.Settings()
			.rarity(Rarity.UNCOMMON);
	
	public static final Item ULTRAPURE_CARBON = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_HYDROGEN = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_ICED_COFFEES = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_CINNABAR = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_GOLD = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_IRON = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_LAZURITE = new BlueCubeItem(UP_SETTINGS);
	public static final Item ULTRAPURE_SILICA = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_YTTRIUM = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_NEODYMIUM = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_COPPER = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_DIAMOND = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_WOLFRAM = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_NETHERITE = new Item(UP_SETTINGS);
	
	public static final Item QUICKSILVER = new Item(UP_SETTINGS);
	
	public static final Item COMPRESSED_ULTRAPURE_CARBON = new Item(UP_SETTINGS);
	
	public static final MercurialPotionItem MERCURIAL_POTION = new MercurialPotionItem(new Item.Settings()
			.maxCount(Items.POTION.getMaxCount()));
	public static final MercurialSplashPotionItem MERCURIAL_SPLASH_POTION = new MercurialSplashPotionItem(new Item.Settings()
			.maxCount(Items.SPLASH_POTION.getMaxCount()));

	public static final Item YTTRIUM_DUST = new Item(new Item.Settings());
	public static final Item IRON_DUST = new Item(new Item.Settings());
	
	public static final Item BROOKITE = new Item(new Item.Settings());
	
	public static final ToolMaterial BROOKITE_MATERIAL = new ToolMaterial() {
		
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.BROOKITE);
		}
		
		@Override
		public float getMiningSpeedMultiplier() {
			return ToolMaterials.IRON.getMiningSpeedMultiplier()*1.15f;
		}
		
		@Override
		public int getMiningLevel() {
			return ToolMaterials.IRON.getMiningLevel();
		}
		
		@Override
		public int getEnchantability() {
			return ToolMaterials.IRON.getEnchantability();
		}
		
		@Override
		public int getDurability() {
			return ToolMaterials.IRON.getDurability()*7/4;
		}
		
		@Override
		public float getAttackDamage() {
			return ToolMaterials.IRON.getAttackDamage();
		}
	};
	
	public static final SwordItem BROOKITE_SWORD = new SwordItem(BROOKITE_MATERIAL, 3, -2.4f, new Item.Settings()) {};
	public static final ShovelItem BROOKITE_SHOVEL = new ShovelItem(BROOKITE_MATERIAL, 1.5f, -3.0f, new Item.Settings()) {};
	public static final PickaxeItem BROOKITE_PICKAXE = new PickaxeItem(BROOKITE_MATERIAL, 1, -2.8f, new Item.Settings()) {};
	public static final AxeItem BROOKITE_AXE = new AxeItem(BROOKITE_MATERIAL, 6, -3.1f, new Item.Settings()) {};
	public static final HoeItem BROOKITE_HOE = new HoeItem(BROOKITE_MATERIAL, -2, -1, new Item.Settings()) {};
	
	public static final DropOfContinuityItem DROP_OF_CONTINUITY = new DropOfContinuityItem(new Item.Settings().maxCount(1));
	public static final DropOfContinuityItem LOOTBOX_OF_CONTINUITY = new DropOfContinuityItem(new Item.Settings().maxCount(1)) {
		@Override public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {}
	};
	
	@ColorProvider("ContinuityItemColorProvider")
	public static final ShifterItem SHIFTER = new ShifterItem(new Item.Settings()
			.maxCount(1));
	@ColorProvider("ContinuityItemColorProvider")
	public static final ProjectorItem PROJECTOR = new ProjectorItem(new Item.Settings()
			.maxCount(1));
	
	public static final MusicDiscItem MUSIC_DISC_PAPILLONS = new MusicDiscItem(14, YSounds.PAPILLONS, new Item.Settings().maxCount(1).rarity(Rarity.RARE)) {};
	public static final MusicDiscItem MUSIC_DISC_VOID = new MusicDiscItem(14, YSounds.VOID_MUSIC, new Item.Settings().maxCount(1).rarity(Rarity.RARE)) {};
	
	public static final Item RUBBLE = new Item(new Item.Settings()) {};
	
	public static final Item PROMETHIUM_SPECK = new Item(new Item.Settings().rarity(Rarity.EPIC));
	public static final Item PROMETHIUM_LUMP = new Item(new Item.Settings().rarity(Rarity.EPIC));
	public static final Item PROMETHIUM_GLOB = new Item(new Item.Settings().rarity(Rarity.EPIC));
	
	public static final Item MAGCAPSULE = new Item(new Item.Settings().maxCount(1));
	public static final Item CUPROSTEEL_INGOT = new Item(new Item.Settings());
	
	public static final LatchReference<Item> COPPER_INGOT = YLatches.create();
	
	public static final LatchReference<Item> CUPROSTEEL_COIL = YLatches.create();
	public static final LatchReference<Item> AMMO_PACK = YLatches.create();
	
	public static final Item EMPTY_AMMO_CAN = new Item(new Item.Settings().maxCount(16));
	public static final AmmoCanItem AMMO_CAN = new AmmoCanItem(new Item.Settings().maxCount(1));

	public static final InRedMultimeterItem INRED_MULTIMETER = new InRedMultimeterItem(new Item.Settings()
			.maxCount(1));
	public static final Item INRED_PCB = new Item(new Item.Settings());
	
	public static void init() {
		Yttr.autoRegister(Registry.ITEM, YItems.class, Item.class);
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface SimpleArmorTexture {
		String value();
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface BuiltinRenderer {
		String value();
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface ColorProvider {
		String value();
	}
}
