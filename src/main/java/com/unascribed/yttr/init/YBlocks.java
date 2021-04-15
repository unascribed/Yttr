package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.abomination.AwareHopperBlock;
import com.unascribed.yttr.block.abomination.SkeletalSorterBlock;
import com.unascribed.yttr.block.decor.CleavedBlock;
import com.unascribed.yttr.block.decor.LampBlock;
import com.unascribed.yttr.block.decor.TableBlock;
import com.unascribed.yttr.block.decor.WallLampBlock;
import com.unascribed.yttr.block.device.CentrifugeBlock;
import com.unascribed.yttr.block.device.PowerMeterBlock;
import com.unascribed.yttr.block.device.SuitStationBlock;
import com.unascribed.yttr.block.mechanism.ChuteBlock;
import com.unascribed.yttr.block.mechanism.DopperBlock;
import com.unascribed.yttr.block.mechanism.FlopperBlock;
import com.unascribed.yttr.block.mechanism.LevitationChamberBlock;
import com.unascribed.yttr.block.mechanism.YttriumPressurePlateBlock;
import com.unascribed.yttr.block.squeeze.DelicaceBlock;
import com.unascribed.yttr.block.squeeze.SqueezeLeavesBlock;
import com.unascribed.yttr.block.squeeze.SqueezeLogBlock;
import com.unascribed.yttr.block.squeeze.SqueezeSaplingBlock;
import com.unascribed.yttr.block.squeeze.SqueezedLeavesBlock;
import com.unascribed.yttr.block.void_.BedrockSmasherBlock;
import com.unascribed.yttr.block.void_.DivingPlateBlock;
import com.unascribed.yttr.block.void_.VoidFluidBlock;
import com.unascribed.yttr.block.void_.VoidGeyserBlock;
import com.unascribed.yttr.util.annotate.RenderLayer;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.PaneBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class YBlocks {

	private static final FabricBlockSettings METALLIC_SETTINGS = FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1);
	
	public static final Block GADOLINITE = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	public static final Block YTTRIUM_BLOCK = new Block(METALLIC_SETTINGS);
	public static final PowerMeterBlock POWER_METER = new PowerMeterBlock(METALLIC_SETTINGS);
	public static final VoidFluidBlock VOID = new VoidFluidBlock(YFluids.VOID, FabricBlockSettings.of(
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
	public static final AwareHopperBlock AWARE_HOPPER = new AwareHopperBlock(METALLIC_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final LevitationChamberBlock LEVITATION_CHAMBER = new LevitationChamberBlock(FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
			.nonOpaque()
		);
	@RenderLayer("cutout_mipped")
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
	@RenderLayer("translucent")
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
	@RenderLayer("cutout_mipped")
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
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZED_LEAVES = new SqueezedLeavesBlock(FabricBlockSettings.copyOf(SQUEEZE_LEAVES)
			.dropsLike(SQUEEZE_LEAVES)
			.dynamicBounds()
		);
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZE_SAPLING = new SqueezeSaplingBlock(new SqueezeSaplingGenerator(), FabricBlockSettings.of(Material.SPONGE)
			.sounds(BlockSoundGroup.GRASS)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.nonOpaque()
		);
	@RenderLayer("translucent")
	public static final Block DELICACE = new DelicaceBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
	@RenderLayer("cutout")
	public static final Block LAMP = new LampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES)
		);
	@RenderLayer("cutout")
	public static final Block FIXTURE = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 12, 10, 6);
	@RenderLayer("cutout")
	public static final Block CAGE_LAMP = new WallLampBlock(FabricBlockSettings.of(Material.METAL)
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			.breakByTool(FabricToolTags.PICKAXES), 10, 6, 10);
	public static final Block YTTRIUM_PLATING = new Block(METALLIC_SETTINGS);
	@RenderLayer("translucent")
	public static final Block GLASSY_VOID_PANE = new PaneBlock(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES)
			.strength(7)
			.nonOpaque()
		) {
		@Override
		public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
			return world.getMaxLightLevel();
		}
	};
	public static final CleavedBlock CLEAVED_BLOCK = new CleavedBlock(FabricBlockSettings.of(Material.PISTON)
			.dynamicBounds());

	public static final YttriumPressurePlateBlock LIGHT_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 15);

	public static final YttriumPressurePlateBlock HEAVY_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 64);
	
	public static final CentrifugeBlock CENTRIFUGE = new CentrifugeBlock(METALLIC_SETTINGS);
	
	public static final DopperBlock DOPPER = new DopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	public static final FlopperBlock FLOPPER = new FlopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	
	public static final DivingPlateBlock DIVING_PLATE = new DivingPlateBlock(METALLIC_SETTINGS);
	
	public static final SuitStationBlock SUIT_STATION = new SuitStationBlock(METALLIC_SETTINGS);
	
	public static final TableBlock TABLE = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
	
	public static final SkeletalSorterBlock SKELETAL_SORTER = new SkeletalSorterBlock(FabricBlockSettings.copyOf(TABLE));
	
	public static void init() {
		Yttr.autoRegister(Registry.BLOCK, YBlocks.class, Block.class);
	}

}
