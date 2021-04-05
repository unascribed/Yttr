package com.unascribed.yttr.init;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.annotate.ConstantColor;
import com.unascribed.yttr.item.CleaverItem;
import com.unascribed.yttr.item.EffectorItem;
import com.unascribed.yttr.item.ReinforcedCleaverItem;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.item.ShearsItem;
import com.unascribed.yttr.item.SnareItem;
import com.unascribed.yttr.item.SwallowableItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.item.block.LevitationChamberBlockItem;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
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
	
	public static final BucketItem VOID_BUCKET = new BucketItem(YFluids.VOID, new Item.Settings()
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
	
	public static final Item EFFECTOR = new EffectorItem(new Item.Settings()
			.group(YItemGroups.MAIN));


	public static void init() {
		Yttr.autoRegister(Registry.ITEM, YItems.class, Item.class);
	}
}
