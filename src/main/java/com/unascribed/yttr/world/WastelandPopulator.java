package com.unascribed.yttr.world;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YTags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.ChunkRandom;

public class WastelandPopulator {

	private static final int FLAGS = 32 | 16 | 2; // SKIP_DROPS | FORCE_STATE | NOTIFY_LISTENERS
	
	public static boolean isEligible(ServerWorld world, WorldChunk chunk) {
		if (world.getBiomeKey(chunk.getPos().getStartPos()).map(k -> k.getValue().toString().equals("yttr:wasteland")).orElse(false)) {
			if (chunk.getBlockState(BlockPos.ORIGIN).getBlock() == YBlocks.SPECIALTY_BEDROCK) return false;
			return true;
		}
		return false;
	}
	
	public static void populate(long worldSeed, ServerWorld world, ChunkPos chunk) {
		BlockPos chunkStart = chunk.getStartPos();
		if (world.getBiomeKey(chunkStart).map(k -> k.getValue().toString().equals("yttr:wasteland")).orElse(false)) {
			if (world.getBlockState(chunkStart).getBlock() == YBlocks.SPECIALTY_BEDROCK) return;
			world.setBlockState(chunkStart, YBlocks.SPECIALTY_BEDROCK.getDefaultState(), 0, 0);
			ChunkRandom rand = new ChunkRandom(worldSeed);
			rand.setPopulationSeed(worldSeed, chunk.getStartX(), chunk.getStartZ());
			BlockPos.Mutable mut = new BlockPos.Mutable();
			BlockPos.Mutable mut2 = new BlockPos.Mutable();
			BlockPos.Mutable mut3 = new BlockPos.Mutable();
			if (rand.nextInt(100) < 5) {
				// staircase to bedrock with optional strip mine
				Direction d = Direction.Type.HORIZONTAL.random(rand);
				int x = chunkStart.getX();
				int z = chunkStart.getZ();
				x += rand.nextInt(16);
				z += rand.nextInt(16);
				mut.set(x, world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)+2, z);
				int distanceSinceTorch = 0;
				int stripMineY = -1;
				if (rand.nextInt(100) < 60) {
					stripMineY = 11+rand.nextInt(6);
				}
				for (int i = 0; i < 1000; i++) {
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					if (rand.nextInt(10) < distanceSinceTorch && !world.getBlockState(mut.offset(d.rotateYCounterclockwise())).isAir()) {
						world.setBlockState(mut, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d.rotateYClockwise()), FLAGS, 0);
						distanceSinceTorch = 0;
					}
					mut.move(Direction.DOWN);
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					mut.move(Direction.DOWN);
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					mut.move(Direction.DOWN);
					if (world.getBlockState(mut).isAir()) {
						world.setBlockState(mut, Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
					}
					mut.move(Direction.UP);
					if (mut.getY() == stripMineY) {
						mut2.set(mut);
						int distanceSinceTorch2 = 0;
						Direction d2 = rand.nextBoolean() ? d.rotateYClockwise() : d.rotateYCounterclockwise();
						for (int j = 0; j < 40+rand.nextInt(80); j++) {
							mut2.move(d2);
							if (!didYouKnowWeHaveVeinMiner(world, mut2, rand)) break;
							mut2.move(Direction.DOWN);
							if (world.getBlockState(mut2).isAir()) {
								world.setBlockState(mut2, Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
							}
							mut2.move(Direction.UP);
							mut2.move(Direction.UP);
							if (!didYouKnowWeHaveVeinMiner(world, mut2, rand)) break;
							if (rand.nextInt(10) < distanceSinceTorch2 && !world.getBlockState(mut2.offset(d2.rotateYCounterclockwise())).isAir()) {
								world.setBlockState(mut2, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d2.rotateYClockwise()), FLAGS, 0);
								distanceSinceTorch2 = 0;
							}
							mut2.move(Direction.DOWN);
							if (j % 3 == 0) {
								Direction d3 = d;
								for (int p = 0; p < 2; p++) {
									int distanceSinceTorch3 = 0;
									if (p == 1) d3 = d3.getOpposite();
									mut3.set(mut2);
									for (int k = 0; k < rand.nextInt(50)+20; k++) {
										mut3.move(d3);
										if (!didYouKnowWeHaveVeinMiner(world, mut3, rand)) break;
										mut3.move(Direction.DOWN);
										if (world.getBlockState(mut3).isAir()) {
											world.setBlockState(mut3, Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
										}
										mut3.move(Direction.UP);
										mut3.move(Direction.UP);
										if (!didYouKnowWeHaveVeinMiner(world, mut3, rand)) break;
										if (rand.nextInt(10) < distanceSinceTorch3 && !world.getBlockState(mut3.offset(d3.rotateYCounterclockwise())).isAir()) {
											world.setBlockState(mut3, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d3.rotateYClockwise()), FLAGS, 0);
											distanceSinceTorch3 = 0;
										}
										mut3.move(Direction.DOWN);
										distanceSinceTorch3++;
									}
								}
							}
						}
					}
					mut.move(Direction.UP);
					mut.move(d);
					distanceSinceTorch++;
				}
			}
			if (rand.nextInt(100) < 15) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/twilight_portal", -2, true, false);
			}
			if (rand.nextInt(300) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/laundromat", 0, true, true);
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/sulfur_goo_farm", 1, true, true);
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				Direction d = Direction.Type.HORIZONTAL.random(rand);
				for (int i = 0; i < 1+rand.nextInt(8); i++) {
					if (!tryPlaceSchematic(rand, world, mut, "yttr:ruined/coke_oven", 0, false, true)) break;
					mut.move(d, 4);
					mut.setY(world.getTopY(Heightmap.Type.WORLD_SURFACE, mut.getX()-1, mut.getZ()-1));
				}
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/blast_furnace", 0, false, true);
			}
			if (rand.nextInt(200) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/quarry", 0, false, true);
			}
		}
	}
	
	public static boolean didYouKnowWeHaveVeinMiner(WorldAccess world, BlockPos pos, Random rand) {
		if (pos.getY() <= 0) return false;
		Set<BlockPos> seen = Sets.newHashSet();
		Set<BlockPos> scan = Sets.newHashSet();
		Set<BlockPos> nextScan = Sets.newHashSet();
		Direction[] directions = Direction.values();
		int i = 0;
		scan.clear();
		nextScan.clear();
		BlockPos start = pos.toImmutable();
		scan.add(start);
		boolean hitUnbreakable = false;
		while (!scan.isEmpty()) {
			if (i++ > 32) break;
			for (BlockPos bp : scan) {
				BlockState bs = world.getBlockState(bp);
				if (bs.isIn(YTags.Block.ORES) || bp.equals(start)) {
					for (Direction d : directions) {
						BlockPos c = bp.offset(d);
						BlockState bs2 = world.getBlockState(c);
						if ((bs2.isIn(YTags.Block.ORES) || (d == Direction.UP && bs2.getBlock() instanceof FallingBlock)) && seen.add(c)) {
							if (bs2.isIn(YTags.Block.LESSER_ORES) && rand.nextInt(40) < seen.size()+2) {
								// eh, we're bored of mining this. let's get back to digging the mine
								break;
							}
							nextScan.add(c);
						}
					}
				} else if (bs.getBlock() instanceof FallingBlock) {
					BlockPos c = bp.up();
					BlockState bs2 = world.getBlockState(c);
					if ((bs2.isIn(YTags.Block.ORES) || bs2.getBlock() instanceof FallingBlock) && seen.add(c)) {
						nextScan.add(c);
					}
				}
				if (bs.isIn(YTags.Block.ORES) || bs.isOf(YBlocks.WASTELAND_DIRT) || bs.isIn(BlockTags.BASE_STONE_OVERWORLD) || bs.getBlock() instanceof FallingBlock) {
					world.setBlockState(bp, Blocks.AIR.getDefaultState(), FLAGS, 0);
				} else if (!bs.isAir() && !bs.getMaterial().isReplaceable() && !bs.isOf(YBlocks.RUINED_WALL_TORCH)) {
					hitUnbreakable = true;
				}
			}
			scan.clear();
			scan.addAll(nextScan);
			nextScan.clear();
		}
		return !hitUnbreakable;
	}
	
	private static boolean tryPlaceSchematic(ChunkRandom rand, ServerWorld world, BlockPos pos, String id, int yOffset, boolean eatDirt, boolean fill) {
		Structure s = world.getStructureManager().getStructure(new Identifier(id));
		BlockRotation rot = BlockRotation.random(rand);
		StructurePlacementData spd = new StructurePlacementData();
		spd.setRotation(rot);
		spd.setUpdateNeighbors(false);
		BlockPos size = s.getRotatedSize(rot);
		BlockPos origin = pos.add(-size.getX()/2, 0, -size.getZ()/2);
		if (origin.getY() == -1) {
			origin = new BlockPos(origin.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, origin.getX(), origin.getZ()), origin.getZ());
		}
		origin = origin.up(yOffset);
		int originY = origin.getY();
		for (BlockPos bpp : BlockPos.iterate(origin, origin.add(s.getRotatedSize(rot)))) {
			BlockState bs = world.getBlockState(bpp);
			if (!bs.isAir() && !bs.getMaterial().isReplaceable() && (!eatDirt || !bs.isOf(YBlocks.WASTELAND_DIRT))) {
				return false;
			}
		}
		List<BlockPos> fillIn = Lists.newArrayList();
		if (fill) {
			spd.addProcessor(new StructureProcessor() {
				
				@Override
				public StructureBlockInfo process(WorldView world,
						BlockPos unk, BlockPos unk2,
						StructureBlockInfo unk3, StructureBlockInfo block,
						StructurePlacementData structurePlacementData) {
					if (block.pos.getY() == originY && block.state.isSideSolid(world, block.pos, Direction.DOWN, SideShapeType.FULL)) {
						if (block.tag == null || !"yttr:quarry_hole".equals(block.tag.getString("metadata"))) {
							fillIn.add(block.pos);
						}
					}
					return block;
				}
				
				@Override
				protected StructureProcessorType<?> getType() {
					return StructureProcessorType.NOP;
				}
			});
		}
		s.place(world, origin, spd, rand);
		for (StructureBlockInfo info : s.getInfosForBlock(origin, spd, Blocks.STRUCTURE_BLOCK, true)) {
			if (info != null && info.state.get(StructureBlock.MODE) == StructureBlockMode.DATA) {
				if (info.tag != null && "yttr:quarry_hole".equals(info.tag.getString("metadata"))) {
					BlockPos.Mutable bp = info.pos.mutableCopy();
					for (int y = info.pos.getY(); y >= 0; y--) {
						bp.setY(y);
						BlockState bs = world.getBlockState(bp);
						if (bs.isOf(Blocks.BEDROCK)) break;
						world.setBlockState(bp, Blocks.AIR.getDefaultState());
					}
				}
			}
		}
		for (BlockPos b : fillIn) {
			BlockPos.Mutable scan = b.mutableCopy();
			scan.move(Direction.DOWN);
			while (world.getBlockState(scan).isAir() || world.getBlockState(scan).getMaterial().isReplaceable()) {
				world.setBlockState(scan, YBlocks.WASTELAND_DIRT.getDefaultState(), FLAGS, 0);
				scan.move(Direction.DOWN);
			}
		}
		return true;
	}
	
}
