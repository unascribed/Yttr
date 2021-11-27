package com.unascribed.yttr.init;

import java.util.Random;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.ClamberBlock;
import com.unascribed.yttr.content.block.ContinuousPlatformBlock;
import com.unascribed.yttr.content.block.abomination.AwareHopperBlock;
import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlock;
import com.unascribed.yttr.content.block.basic.BasicFacingBlock;
import com.unascribed.yttr.content.block.basic.BasicHorizontalFacingBlock;
import com.unascribed.yttr.content.block.big.DSUBlock;
import com.unascribed.yttr.content.block.big.MagtankBlock;
import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.block.decor.TableBlock;
import com.unascribed.yttr.content.block.decor.WallLampBlock;
import com.unascribed.yttr.content.block.device.CentrifugeBlock;
import com.unascribed.yttr.content.block.device.EncasedVoidFilterBlock;
import com.unascribed.yttr.content.block.device.PowerMeterBlock;
import com.unascribed.yttr.content.block.device.SuitStationBlock;
import com.unascribed.yttr.content.block.device.VoidFilterBlock;
import com.unascribed.yttr.content.block.lazor.LazorBeamBlock;
import com.unascribed.yttr.content.block.lazor.LazorEmitterBlock;
import com.unascribed.yttr.content.block.mechanism.ChuteBlock;
import com.unascribed.yttr.content.block.mechanism.CuprosteelPressurePlateBlock;
import com.unascribed.yttr.content.block.mechanism.DopperBlock;
import com.unascribed.yttr.content.block.mechanism.FlopperBlock;
import com.unascribed.yttr.content.block.mechanism.LevitationChamberBlock;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlock;
import com.unascribed.yttr.content.block.mechanism.VoidCauldronBlock;
import com.unascribed.yttr.content.block.mechanism.YttriumButtonBlock;
import com.unascribed.yttr.content.block.mechanism.YttriumPressurePlateBlock;
import com.unascribed.yttr.content.block.natural.DelicaceBlock;
import com.unascribed.yttr.content.block.natural.RootOfContinuityBlock;
import com.unascribed.yttr.content.block.natural.SqueezeLeavesBlock;
import com.unascribed.yttr.content.block.natural.SqueezeLogBlock;
import com.unascribed.yttr.content.block.natural.SqueezeSaplingBlock;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlock;
import com.unascribed.yttr.content.block.note.BoggedHighNoteBlock;
import com.unascribed.yttr.content.block.note.BoggedLowNoteBlock;
import com.unascribed.yttr.content.block.note.BoggedNoteBlock;
import com.unascribed.yttr.content.block.note.HighNoteBlock;
import com.unascribed.yttr.content.block.note.LowNoteBlock;
import com.unascribed.yttr.content.block.ruined.RCStyleMultiblock;
import com.unascribed.yttr.content.block.ruined.RuinedPipeBlock;
import com.unascribed.yttr.content.block.void_.BedrockSmasherBlock;
import com.unascribed.yttr.content.block.void_.DivingPlateBlock;
import com.unascribed.yttr.content.block.void_.DormantVoidGeyserBlock;
import com.unascribed.yttr.content.block.void_.ErodedBedrockBlock;
import com.unascribed.yttr.content.block.void_.MagtubeBlock;
import com.unascribed.yttr.content.block.void_.PureVoidFluidBlock;
import com.unascribed.yttr.content.block.void_.VoidFluidBlock;
import com.unascribed.yttr.content.block.void_.VoidGeyserBlock;
import com.unascribed.yttr.mixin.accessor.AccessorBlock;
import com.unascribed.yttr.util.LatchReference;
import com.unascribed.yttr.util.annotate.RenderLayer;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FernBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class YBlocks {

	private static final FabricBlockSettings METALLIC_SETTINGS = FabricBlockSettings.of(Material.METAL)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1);
	
	public static final BlockSoundGroup HOLLOWHUGE_SOUNDS = new BlockSoundGroup(0.8f, 1, YSounds.HOLLOWBREAKHUGE, YSounds.HOLLOWSTEP, YSounds.HOLLOWPLACEHUGE, YSounds.HOLLOWHIT, YSounds.HOLLOWSTEP);
//	public static final BlockSoundGroup HOLLOW_SOUNDS = new BlockSoundGroup(0.8f, 1, YSounds.HOLLOWBREAK, YSounds.HOLLOWSTEP, YSounds.HOLLOWPLACE, YSounds.HOLLOWHIT, YSounds.HOLLOWSTEP);
	
	private static final FabricBlockSettings HOLLOWHUGE_SETTINGS = FabricBlockSettings.copyOf(METALLIC_SETTINGS)
			.sounds(HOLLOWHUGE_SOUNDS)
			.strength(8)
			.nonOpaque();
//	private static final FabricBlockSettings HOLLOW_SETTINGS = FabricBlockSettings.copyOf(METALLIC_SETTINGS)
//			.sounds(HOLLOW_SOUNDS);
	
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
			new FabricMaterialBuilder(MapColor.WATER_BLUE)
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
	@RenderLayer("translucent")
	public static final PureVoidFluidBlock PURE_VOID = new PureVoidFluidBlock(YFluids.PURE_VOID, FabricBlockSettings.of(
			new FabricMaterialBuilder(MapColor.WATER_BLUE)
				.allowsMovement()
				.lightPassesThrough()
				.notSolid()
				.liquid()
				.build()
			)
		.noCollision()
		.strength(100)
		.dropsNothing()
	);
	public static final FluidBlock CORE_LAVA = new FluidBlock(YFluids.CORE_LAVA, FabricBlockSettings.of(
			new FabricMaterialBuilder(MapColor.BRIGHT_RED)
				.allowsMovement()
				.lightPassesThrough()
				.notSolid()
				.liquid()
				.build()
			)
		.noCollision()
		.strength(100)
		.dropsNothing()
		.luminance(15)
	) {
		@Override
		public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
			// no-op to prevent deadlock, and as core lava doesn't flow
		}
		@Override
		public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
			return Fluids.LAVA;
		}
		@Override
		public PistonBehavior getPistonBehavior(BlockState state) {
			return PistonBehavior.BLOCK;
		}
	};
	
	
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
	public static final DormantVoidGeyserBlock DORMANT_VOID_GEYSER = new DormantVoidGeyserBlock(FabricBlockSettings.copyOf(VOID_GEYSER)
			.nonOpaque());
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
			.drops(new Identifier("yttr", "blocks/squeeze_leaves"))
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
			.dynamicBounds()
			.nonOpaque());

	public static final YttriumPressurePlateBlock LIGHT_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 15);

	public static final YttriumPressurePlateBlock HEAVY_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 64);
	
	public static final CentrifugeBlock CENTRIFUGE = new CentrifugeBlock(METALLIC_SETTINGS);
	
	public static final DopperBlock DOPPER = new DopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	public static final FlopperBlock FLOPPER = new FlopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	
	public static final DivingPlateBlock DIVING_PLATE = new DivingPlateBlock(METALLIC_SETTINGS);
	
	public static final SuitStationBlock SUIT_STATION = new SuitStationBlock(METALLIC_SETTINGS);
	
	public static final TableBlock TABLE = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
	
	public static final SkeletalSorterBlock SKELETAL_SORTER = new SkeletalSorterBlock(FabricBlockSettings.copyOf(TABLE));
	
	public static final ReplicatorBlock REPLICATOR = new ReplicatorBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK)
			.nonOpaque()
			.noCollision()
		);
	
	public static final VoidCauldronBlock VOID_CAULDRON = new VoidCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)
			.dropsLike(Blocks.CAULDRON));
	
	public static final Block ULTRAPURE_CARBON_BLOCK = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	
	public static final Block COMPRESSED_ULTRAPURE_CARBON_BLOCK = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(6)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 2)
		);
	
	public static final EncasedVoidFilterBlock ENCASED_VOID_FILTER = new EncasedVoidFilterBlock(FabricBlockSettings.copyOf(BEDROCK_SMASHER));
	public static final VoidFilterBlock VOID_FILTER = new VoidFilterBlock(FabricBlockSettings.copyOf(METALLIC_SETTINGS)
			.resistance(4000));
	
	public static final ErodedBedrockBlock ERODED_BEDROCK = new ErodedBedrockBlock(FabricBlockSettings.of(Material.STONE)
			.breakByTool(FabricToolTags.PICKAXES, 3)
			.strength(45, 0)
		);
	
	public static final LazorBeamBlock LAZOR_BEAM = new LazorBeamBlock(FabricBlockSettings.of(Material.AIR)
			.collidable(false)
			.dropsNothing()
			.ticksRandomly()
			.strength(-1, 10000)
			.luminance(15)
		);
	
	@RenderLayer("cutout")
	public static final LazorEmitterBlock LAZOR_EMITTER = new LazorEmitterBlock(METALLIC_SETTINGS);
	
	public static final MagtankBlock MAGTANK = new MagtankBlock(HOLLOWHUGE_SETTINGS);
//	public static final GiantsBlock GIANT_COBBLESTONE = new GiantsBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE));
	public static final DSUBlock DSU = new DSUBlock(HOLLOWHUGE_SETTINGS);
	
	public static final Block BROOKITE_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE)
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 2)
		) {
		@Override
		protected int getExperienceWhenMined(Random rand) {
			return 1+rand.nextInt(5);
		}
	};
	
	public static final RootOfContinuityBlock ROOT_OF_CONTINUITY = new RootOfContinuityBlock(FabricBlockSettings.of(Material.GLASS)
			.breakByTool(FabricToolTags.PICKAXES)
			.strength(20)
			.luminance(4)
			.sounds(RootOfContinuityBlock.SOUND_GROUP)
			.requiresTool());
	
	public static final YttriumButtonBlock YTTRIUM_BUTTON = new YttriumButtonBlock(FabricBlockSettings.of(Material.DECORATION)
			.strength(1)
			.breakByTool(FabricToolTags.PICKAXES)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.noCollision());
	
	public static final Block BROOKITE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE)
			.strength(3)
			.requiresTool()
			.sounds(BlockSoundGroup.NETHERITE)
			.breakByHand(false)
			.breakByTool(FabricToolTags.PICKAXES, 1)
		);
	
	public static final AirBlock TEMPORARY_LIGHT_AIR = new AirBlock(FabricBlockSettings.of(Material.AIR)
			.noCollision()
			.air()
			.nonOpaque()
			.luminance(9)
			.ticksRandomly()
		) {
		@Override
		public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
			if (random.nextInt(4) == 0) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	};
	
	public static final AirBlock PERMANENT_LIGHT_AIR = new AirBlock(FabricBlockSettings.of(Material.AIR)
			.noCollision()
			.air()
			.nonOpaque()
			.luminance(15)
		) {};
		
	public static final Block NETHERTUFF = new Block(FabricBlockSettings.copyOf(Blocks.NETHERRACK)
			.strength(1.4f, 0.2f)
			.allowsSpawning((state, world, pos, et) -> false)
		);
	
	@RenderLayer("cutout")
	public static final MagtubeBlock MAGTUBE = new MagtubeBlock(METALLIC_SETTINGS);
	
	public static final Block WASTELAND_DIRT = new Block(FabricBlockSettings.copyOf(Blocks.DIRT)
			.strength(0.8f)
		);
	
	@RenderLayer("cutout")
	public static final FernBlock WASTELAND_GRASS = new FernBlock(FabricBlockSettings.copyOf(Blocks.GRASS)
			.breakByTool(FabricToolTags.SHOVELS)) {
		@Override
		protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
			return floor.isOf(YBlocks.WASTELAND_DIRT);
		}
		@Override
		public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
			return false;
		}
	};
	
	public static final PillarBlock WASTELAND_LOG = new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG)
			.strength(1.0f)
		);
	
	public static final Block WASTELAND_STONE = new Block(FabricBlockSettings.copyOf(Blocks.STONE)
			.strength(1.0f)
		);
	
	private static final Block.Settings RUINED_SETTINGS = FabricBlockSettings.copyOf(Blocks.DIRT)
			.sounds(BlockSoundGroup.BASALT)
			.drops(new Identifier("yttr", "blocks/ruined"));
	private static final Block.Settings RUINED_UNCL_SETTINGS = FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.noCollision()
			.drops(new Identifier("yttr", "blocks/ruined"));
	private static final Block.Settings RUINED_PARTIAL_SETTINGS = FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.nonOpaque()
			.drops(new Identifier("yttr", "blocks/ruined"));
	private static final Block.Settings RUINED_TORCH_SETTINGS = FabricBlockSettings.copyOf(RUINED_UNCL_SETTINGS)
			.luminance(2)
			.drops(new Identifier("yttr", "blocks/ruined"));
	
	private static <T extends Block> T ruinedDevice(T block) {
		((AccessorBlock)block).yttr$setTranslationKey("block.yttr.ruined_device");
		return block;
	}
	
	private static <T extends Block> T ruinedConstruct(T block) {
		((AccessorBlock)block).yttr$setTranslationKey("block.yttr.ruined_construct");
		return block;
	}
	
	public static final Block RUINED_COBBLESTONE = new Block(RUINED_SETTINGS);
	public static final Block RUINED_BRICKS = new Block(RUINED_SETTINGS);
	
	public static final Block RUINED_CONTAINER = new Block(FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.drops(new Identifier("yttr", "blocks/ruined_container")));
	@RenderLayer("cutout")
	public static final Block RUINED_TANK = new Block(RUINED_SETTINGS) {
		private VoxelShape SHAPE = createCuboidShape(2, 0, 2, 14, 16, 14);
		@Override
		public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }
	};
	
	public static final Block RUINED_DEVICE_BC_1 = ruinedDevice(new BasicHorizontalFacingBlock(RUINED_SETTINGS));
	public static final Block RUINED_DEVICE_BC_2 = new BasicFacingBlock(RUINED_PARTIAL_SETTINGS);
	
	public static final Block RUINED_DEVICE_GT_1 = ruinedDevice(new Block(RUINED_SETTINGS));
	
	public static final Block RUINED_DEVICE_RP_1 = ruinedDevice(new BasicFacingBlock(RUINED_SETTINGS));
	
	public static final Block RUINED_CONSTRUCT_RC_1 = ruinedConstruct(new RCStyleMultiblock(1, 1, 1, RUINED_SETTINGS));
	public static final Block RUINED_CONSTRUCT_RC_2 = ruinedConstruct(new RCStyleMultiblock(1, 3, 0, RUINED_SETTINGS));
	
	@RenderLayer("cutout")
	public static final Block RUINED_DEVICE_FO_1 = ruinedDevice(new Block(RUINED_PARTIAL_SETTINGS));
	
	@RenderLayer("cutout")
	public static final Block RUINED_PIPE = new RuinedPipeBlock(RUINED_SETTINGS);
	@RenderLayer("cutout")
	public static final Block RUINED_FRAME = new RuinedPipeBlock(RUINED_SETTINGS) {
		@Override
		public boolean connectsTo(BlockState bs, Direction face) {
			return bs.isOf(this);
		}
	};
	@RenderLayer("cutout")
	public static final Block RUINED_TUBE = new RuinedPipeBlock(RUINED_SETTINGS);
	
	@RenderLayer("cutout")
	public static final Block RUINED_TORCH = new TorchBlock(RUINED_TORCH_SETTINGS, null) {
		@Override
		public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {}
	};
	@RenderLayer("cutout")
	public static final Block RUINED_WALL_TORCH = new WallTorchBlock(RUINED_TORCH_SETTINGS, null) {
		@Override
		public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {}
	};
	public static final Block RUINED_LEVER = new LeverBlock(RUINED_UNCL_SETTINGS) {
		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			if (player.isCreative()) return super.onUse(state, world, pos, player, hand, hit);
			world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, 0.6f);
			world.breakBlock(pos, false);
			return ActionResult.SUCCESS;
		}
		@Override
		public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {}
		@Override
		public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) { return 0; }
		@Override
		public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) { return 0; }
	};
	
	public static final Block SPECIALTY_BEDROCK = new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).dropsNothing());
	@RenderLayer("translucent")
	public static final ContinuousPlatformBlock CONTINUOUS_PLATFORM = new ContinuousPlatformBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK)
			.dropsNothing()
			.ticksRandomly()
			.nonOpaque()
			.blockVision((state, world, pos) -> false)
			.suffocates((state, world, pos) -> false)
			.allowsSpawning((state, world, pos, entity) -> false)
			.luminance(8)
		);
	
	public static final HighNoteBlock HIGH_NOTE_BLOCK = new HighNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final LowNoteBlock LOW_NOTE_BLOCK = new LowNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	
	public static final BoggedNoteBlock BOGGED_NOTE_BLOCK = new BoggedNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final BoggedHighNoteBlock BOGGED_HIGH_NOTE_BLOCK = new BoggedHighNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final BoggedLowNoteBlock BOGGED_LOW_NOTE_BLOCK = new BoggedLowNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	
	public static final ClamberBlock CLAMBER_BLOCK = new ClamberBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.nonOpaque()
		);
	public static final ClamberBlock SOUL_CLAMBER_BLOCK = new ClamberBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.materialColor(MapColor.BROWN)
			.nonOpaque()
		);
	
	public static final Block SOUL_PLANKS = new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.materialColor(MapColor.BROWN)
		);
	
	public static final LatchReference<Block> COPPER_ORE = YLatches.create();
	
	public static final Block CUPROSTEEL_BLOCK = new Block(METALLIC_SETTINGS);
	public static final CuprosteelPressurePlateBlock CUPROSTEEL_PLATE = new CuprosteelPressurePlateBlock(METALLIC_SETTINGS);
	
	public static void init() {
		Yttr.autoRegister(Registry.BLOCK, YBlocks.class, Block.class);
	}

}
