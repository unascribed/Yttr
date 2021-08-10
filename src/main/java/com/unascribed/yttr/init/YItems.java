package com.unascribed.yttr.init;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.item.CleaverItem;
import com.unascribed.yttr.item.EffectorItem;
import com.unascribed.yttr.item.HornItem;
import com.unascribed.yttr.item.ReinforcedCleaverItem;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.item.ShearsItem;
import com.unascribed.yttr.item.SnareItem;
import com.unascribed.yttr.item.SpectralAxeItem;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.item.SwallowableItem;
import com.unascribed.yttr.item.VoidBucketItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.item.block.LevitationChamberBlockItem;
import com.unascribed.yttr.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.item.block.SkeletalSorterBlockItem;
import com.unascribed.yttr.item.potion.MercurialPotionItem;
import com.unascribed.yttr.item.potion.MercurialSplashPotionItem;
import com.unascribed.yttr.util.annotate.ConstantColor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
	public static final BlockItem FLOPPER = new HornItem(YBlocks.FLOPPER, new Item.Settings().group(YItemGroups.MAIN));
	public static final BlockItem DIVING_PLATE = createNormalBlockItem(YBlocks.DIVING_PLATE);
	public static final BlockItem SUIT_STATION = createNormalBlockItem(YBlocks.SUIT_STATION);
	public static final BlockItem TABLE = createNormalBlockItem(YBlocks.TABLE);
	public static final BlockItem ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.ULTRAPURE_CARBON_BLOCK, new Item.Settings().group(YItemGroups.MAIN).rarity(Rarity.UNCOMMON));
	public static final BlockItem COMPRESSED_ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.COMPRESSED_ULTRAPURE_CARBON_BLOCK, new Item.Settings().group(YItemGroups.MAIN).rarity(Rarity.UNCOMMON));
	public static final BlockItem ENCASED_VOID_FILTER = createNormalBlockItem(YBlocks.ENCASED_VOID_FILTER);
	public static final BlockItem VOID_FILTER = createNormalBlockItem(YBlocks.VOID_FILTER);
	
	public static final BlockItem LAMP = new LampBlockItem(YBlocks.LAMP, new Item.Settings()
			.group(YItemGroups.LAMP));

	public static final BlockItem FIXTURE = new LampBlockItem(YBlocks.FIXTURE, new Item.Settings()
			.group(YItemGroups.LAMP));

	public static final BlockItem CAGE_LAMP = new LampBlockItem(YBlocks.CAGE_LAMP, new Item.Settings()
			.group(YItemGroups.LAMP));
	
	public static final BlockItem AWARE_HOPPER = new BlockItem(YBlocks.AWARE_HOPPER, new Item.Settings()
			.group(YItemGroups.MAIN).maxCount(1));

	public static final BlockItem LEVITATION_CHAMBER = new LevitationChamberBlockItem(YBlocks.LEVITATION_CHAMBER, new Item.Settings()
			.group(YItemGroups.MAIN));
	
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_RIGHT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.RIGHT, new Item.Settings()
			.group(YItemGroups.MAIN));
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_LEFT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.LEFT, new Item.Settings()
			.group(YItemGroups.MAIN));
	
	public static final ReplicatorBlockItem REPLICATOR = new ReplicatorBlockItem(YBlocks.REPLICATOR, new Item.Settings()
			.group(YItemGroups.MAIN));

	private static BlockItem createNormalBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings().group(YItemGroups.MAIN));
	}

	public static final Item YTTRIUM_INGOT = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	
	public static final Item YTTRIUM_NUGGET = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	
	public static final Item XL_IRON_INGOT = new Item(new Item.Settings()
			.maxCount(16)
			.group(YItemGroups.MAIN));
	
	public static final VoidBucketItem VOID_BUCKET = new VoidBucketItem(new Item.Settings()
			.recipeRemainder(Items.BUCKET)
			.maxCount(1)
			.group(YItemGroups.MAIN));
	
	public static final RifleItem RIFLE = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(YItemGroups.MAIN), 1, 1, false, 0x3E5656);
	
	public static final RifleItem RIFLE_REINFORCED = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(YItemGroups.MAIN), 0.85f, 1, true, 0x223333);
	
	public static final RifleItem RIFLE_OVERCLOCKED = new RifleItem(new Item.Settings()
			.maxCount(1)
			.group(YItemGroups.MAIN), 1.65f, 2, false, 0x111111);
	
	public static final SnareItem SNARE = new SnareItem(new Item.Settings()
			.maxDamage(40960)
			.group(YItemGroups.MAIN));
	
	public static final ShearsItem SHEARS = new ShearsItem(new Item.Settings()
			.maxDamage(512)
			.group(YItemGroups.MAIN));
	
	public static final Item BEDROCK_SHARD = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	
	public static final Item DELICACE = new SwallowableItem(new Item.Settings()
			.group(YItemGroups.MAIN)
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
			.group(YItemGroups.MAIN)
			.maxCount(16)
			.recipeRemainder(Items.GLASS_BOTTLE));
	
	public static final Item LOGO = new Item(new Item.Settings()) {
		@Override
		public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {}
	};
	
	public static final Item CLEAVER = new CleaverItem(new Item.Settings()
			.maxDamage(1562)
			.group(YItemGroups.MAIN));
	
	public static final Item REINFORCED_CLEAVER = new ReinforcedCleaverItem(new Item.Settings()
			.maxDamage(3072)
			.fireproof()
			.group(YItemGroups.MAIN));
	
	public static final EffectorItem EFFECTOR = new EffectorItem(new Item.Settings()
			.maxCount(1)
			.group(YItemGroups.MAIN));
	
	public static final Item NEODYMIUM_DUST = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	public static final Item NEODYMIUM_DISC = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	
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
	
	public static final SuitArmorItem SUIT_HELMET = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.HEAD, new Item.Settings()
			.fireproof()
			.group(YItemGroups.MAIN));
	
	public static final SuitArmorItem SUIT_CHESTPLATE = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.CHEST, new Item.Settings()
			.fireproof()
			.group(YItemGroups.MAIN));
	
	public static final SuitArmorItem SUIT_LEGGINGS = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.LEGS, new Item.Settings()
			.fireproof()
			.group(YItemGroups.MAIN));
	
	public static final SuitArmorItem SUIT_BOOTS = new SuitArmorItem(SUIT_MATERIAL, EquipmentSlot.FEET, new Item.Settings()
			.fireproof()
			.group(YItemGroups.MAIN));
	
	public static final Item ARMOR_PLATING = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	
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
	}, EquipmentSlot.HEAD, new Item.Settings()
			.group(YItemGroups.MAIN)) {


		@Override
		public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
			return ImmutableMultimap.of();
		}
		
	};
	
	public static final SpectralAxeItem SPECTRAL_AXE = new SpectralAxeItem();
	
	private static final Item.Settings UP_SETTINGS = new Item.Settings()
			.group(YItemGroups.MAIN)
			.rarity(Rarity.UNCOMMON);
	
	public static final Item ULTRAPURE_CARBON = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_HYDROGEN = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_ICED_COFFEES = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_CINNABAR = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_GOLD = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_IRON = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_LAZURITE = new Item(UP_SETTINGS);
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
			.maxCount(Items.POTION.getMaxCount())
			.group(YItemGroups.POTION));
	public static final MercurialSplashPotionItem MERCURIAL_SPLASH_POTION = new MercurialSplashPotionItem(new Item.Settings()
			.maxCount(Items.SPLASH_POTION.getMaxCount())
			.group(YItemGroups.POTION));

	public static final Item YTTRIUM_DUST = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));
	public static final Item IRON_DUST = new Item(new Item.Settings()
			.group(YItemGroups.MAIN));

	public static void init() {
		Yttr.autoRegister(Registry.ITEM, YItems.class, Item.class);
	}
}
