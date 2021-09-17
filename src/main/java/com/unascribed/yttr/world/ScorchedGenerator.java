package com.unascribed.yttr.world;

import java.util.Arrays;
import java.util.List;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorBiomeArray;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.state.property.Property;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;

public class ScorchedGenerator {

	private static final int HORIZONTAL_SECTION_COUNT = AccessorBiomeArray.yttr$getHorizontalSectionCount();
	private static final int VERTICAL_SECTION_COUNT = AccessorBiomeArray.yttr$getVerticalSectionCount();
	
	public static void generate(long worldSeed, ChunkRegion region, StructureAccessor accessor) {
		if (region.toServerWorld().getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			BlockPos.Mutable bp = new BlockPos.Mutable(0, 0, 0);
			Chunk chunk = region.getChunk(region.getCenterChunkX(), region.getCenterChunkZ());
			if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						bp.set(x, 0, z);
						chunk.setBlockState(bp, Blocks.BARRIER.getDefaultState(), false);
						for (int y = 1; y < 4; y++) {
							bp.set(x, y, z);
							chunk.setBlockState(bp, YBlocks.CORE_LAVA.getDefaultState(), false);
						}
						bp.set(x, 4, z);
						chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
						bp.set(x, 5, z);
						chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
					}
				}
			}
			bp.setY(127);
			if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				ChunkRandom rand = new ChunkRandom(worldSeed);
				OctaveSimplexNoiseSampler noise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(1, 4, 8));
				OctaveSimplexNoiseSampler fireNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 10));
				OctaveSimplexNoiseSampler heightsBNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 3, 6));
				OctaveSimplexNoiseSampler heightsTNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 4));
				rand.setPopulationSeed(31*worldSeed, chunk.getPos().getStartX(), chunk.getPos().getStartZ());
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 120; y < 128; y++) {
							bp.set(x, y, z);
							if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
							}
						}
						int bX = (chunk.getPos().getStartX()+x);
						int bZ = (chunk.getPos().getStartZ()+z);
						double height = (noise.sample(bX/200D, bZ/200D, true)+0.2)*6;
						int lastY = 128;
						if (height < 0) {
							for (int y = 128; y > 128+(height*8); y--) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
								lastY = y-1;
							}
						} else {
							if (height > 3) {
								height = 3+((height-3)*10);
							}
							for (int y = 128; y < 128+height; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
								lastY = y;
							}
						}
						if (rand.nextDouble()*2 < (fireNoise.sample(bX/20D, bZ/20D, true)-0.2)) {
							bp.set(bX, lastY, bZ);
							if (!region.getBlockState(bp).isAir()) {
								bp.set(bX, lastY+1, bZ);
								region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
							}
						}
						
						double bh = heightsBNoise.sample(bX/100D, bZ/100D, true)*12;
						if (bh > 0) {
							double th = heightsTNoise.sample(bX/200D, bZ/200D, true)*6;
							int lastYH = 0;
							int by = (int)(220-bh);
							for (int y = by; y < 220+th; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
								lastYH = y;
							}
							if (lastYH > 0 && rand.nextDouble() < (fireNoise.sample(bX/30D, bZ/30D, false)+0.2)) {
								bp.set(bX, lastYH, bZ);
								if (!region.getBlockState(bp).isAir()) {
									bp.set(bX, lastYH+1, bZ);
									region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
								}
							}
						}
					}
				}
				if (accessor.shouldGenerateStructures() && rand.nextInt(20) == 0) {
					Structure s = region.toServerWorld().getStructureManager().getStructure(new Identifier("yttr", "terminus_house"));
					BlockRotation rot = BlockRotation.random(rand);
					List<BlockPos> chains = Lists.newArrayList();
					StructurePlacementData spd = new StructurePlacementData();
					spd.setRotation(rot);
					BlockPos origin = new BlockPos(
							(chunk.getPos().getStartX()-8)+rand.nextInt(24),
							(190+(rand.nextInt(30)))-s.getSize().getY(),
							(chunk.getPos().getStartZ()-8)+rand.nextInt(24)
					);
					boolean success = true;
					for (int i = 0; i < 3; i++) {
						success = true;
						for (BlockPos bpp : BlockPos.iterate(origin, origin.add(s.getRotatedSize(rot)))) {
							BlockState bs = region.getBlockState(bpp);
							if (!bs.isAir()) {
								if (!bs.isOf(YBlocks.NETHERTUFF)) {
									// we probably ran into another already-generated house, so just bail entirely to avoid weird generation
									return;
								}
								success = false;
								origin = origin.down(bpp.getY()-origin.getY());
								break;
							}
						}
						if (success) break;
					}
					if (!success) return;
					boolean foundAllAnchors = true;
					for (StructureBlockInfo info : s.getInfosForBlock(origin, spd, Blocks.STRUCTURE_BLOCK, true)) {
						if (info != null && info.state.get(StructureBlock.MODE) == StructureBlockMode.DATA) {
							if (info.tag != null && "yttr:chain".equals(info.tag.getString("metadata"))) {
								bp.set(info.pos);
								boolean foundAnchor = false;
								for (int i = 0; i < 10; i++) {
									bp.move(Direction.UP);
									if (!region.getBlockState(bp).isAir()) {
										foundAnchor = true;
										break;
									}
								}
								if (!foundAnchor) {
									foundAllAnchors = false;
									break;
								}
								chains.add(info.pos.toImmutable());
							}
						}
					}
					if (foundAllAnchors) {
						boolean warped = rand.nextBoolean();
						spd.addProcessor(new StructureProcessor() {
							
							private final ImmutableBiMap<Block, Block> SWAP = ImmutableBiMap.<Block, Block>builder()
									.put(Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON)
									.put(Blocks.CRIMSON_DOOR, Blocks.WARPED_DOOR)
									.put(Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE)
									.put(Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE)
									.put(Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS)
									.put(Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE)
									.put(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM)
									.put(Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS)
									.put(Blocks.CRIMSON_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE)
									.put(Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS)
									.put(Blocks.CRIMSON_SIGN, Blocks.WARPED_SIGN)
									.put(Blocks.CRIMSON_SLAB, Blocks.WARPED_SLAB)
									.put(Blocks.CRIMSON_STAIRS, Blocks.WARPED_STAIRS)
									.put(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM)
									.put(Blocks.CRIMSON_TRAPDOOR, Blocks.WARPED_TRAPDOOR)
									.put(Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_WALL_SIGN)
									.put(Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
									.put(Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM)
									.put(Blocks.POTTED_CRIMSON_FUNGUS, Blocks.POTTED_WARPED_FUNGUS)
									.put(Blocks.POTTED_CRIMSON_ROOTS, Blocks.POTTED_WARPED_ROOTS)
									.build();
							
							@Override
							public StructureBlockInfo process(WorldView world,
									BlockPos unk, BlockPos unk2,
									StructureBlockInfo unk3, StructureBlockInfo block,
									StructurePlacementData structurePlacementData) {
								if (warped) {
									Block swapped = SWAP.getOrDefault(block.state.getBlock(), SWAP.inverse().get(block.state.getBlock()));
									if (swapped != null) {
										BlockState bs = swapped.getDefaultState();
										for (Property prop : block.state.getProperties()) {
											if (bs.contains(prop)) {
												bs = bs.with(prop, block.state.get(prop));
											}
										}
										return new StructureBlockInfo(block.pos, bs, block.tag);
									}
								}
								if (block.tag != null && block.tag.contains("Items")) {
									NbtList items = block.tag.getList("Items", NbtType.COMPOUND);
									if (items.size() == 1) {
										ItemStack item = ItemStack.fromNbt(items.getCompound(0));
										if (item.getItem() == Items.PAPER && item.hasCustomName()) {
											Identifier id = new Identifier(item.getName().asString());
											Identifier finId = new Identifier(id.getNamespace(), "chests/"+id.getPath());
											NbtCompound newTag = block.tag.copy();
											newTag.remove("Items");
											newTag.putString("LootTable", finId.toString());
											return new StructureBlockInfo(block.pos, block.state, newTag);
										}
									}
								}
								return block;
							}
							
							@Override
							protected StructureProcessorType<?> getType() {
								return StructureProcessorType.NOP;
							}
						});
						System.out.println(origin);
						s.place(region, origin, spd, rand);
						for (BlockPos chain : chains) {
							bp.set(chain);
							for (int i = 0; i < 10; i++) {
								if (region.getBlockState(bp).isAir() || region.getBlockState(bp).isOf(Blocks.STRUCTURE_BLOCK)) {
									region.setBlockState(bp, Blocks.CHAIN.getDefaultState(), 3);
								} else {
									break;
								}
								bp.move(Direction.UP);
							}
						}
					}
				}
			}
		}
	}

	public static void amendBiomes(ChunkRegion region, Chunk chunk) {
		if (region.toServerWorld().getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			Biome[] data = ((AccessorBiomeArray)chunk.getBiomeArray()).yttr$getData();
			int summitIdx = (128 >> 2) << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
			int terminusIdx = (192 >> 2) << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
			Arrays.fill(data, summitIdx, terminusIdx, region.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier("yttr", "scorched_summit")));
			Arrays.fill(data, terminusIdx, data.length, region.getRegistryManager().get(Registry.BIOME_KEY).get(new Identifier("yttr", "scorched_terminus")));
		}
	}
	
}
