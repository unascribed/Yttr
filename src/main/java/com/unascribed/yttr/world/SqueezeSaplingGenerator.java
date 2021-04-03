package com.unascribed.yttr.world;

import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Random;

import com.unascribed.yttr.block.SqueezeLogBlock;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import static net.minecraft.util.math.Direction.*;

public class SqueezeSaplingGenerator extends SaplingGenerator {

	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
		// no thanks i'll do it myself
		return null;
	}
	
	@Override
	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState _unused_nullable, Random random) {
		return generate(world, pos, random);
	}
		
	public boolean generate(WorldAccess world, BlockPos pos, Random random) {
		if (!YBlocks.SQUEEZE_SAPLING.canPlaceAt(null, world, pos)) return false;
		Map<BlockPos, BlockState> plan = Maps.newLinkedHashMap();
		
		BlockPos.Mutable turt = pos.mutableCopy();
		BlockPos.Mutable turt2 = new BlockPos.Mutable();
		
		BlockState LOG = YBlocks.SQUEEZE_LOG.getDefaultState().with(SqueezeLogBlock.ALIVE, true);
		BlockState LEAVES = YBlocks.SQUEEZE_LEAVES.getDefaultState().with(LeavesBlock.DISTANCE, 1);
		
		int baseStemHeight = random.nextInt(3)+2;
		int segments = random.nextInt(4)+3;

		for (int i = 0; i < baseStemHeight; i++) {
			if (!canReplace(plan, world, turt, false)) return false;
			plan.put(turt.toImmutable(), LOG);
			turt.move(UP);
		}
		
		for (int i = 0; i < segments; i++) {
			if (!canReplace(plan, world, turt, false)) return false;
			plan.put(turt.toImmutable(), LOG);
			if (i == segments-1) {
				for (Direction dir : Direction.values()) {
					if (dir == DOWN) continue;
					turt2.set(turt);
					turt2.move(dir);
					if (canReplace(plan, world, turt2, true)) {
						plan.put(turt2.toImmutable(), LEAVES);
					}
				}
			} else {
				int r = segments-i;
				BlockPos[] branchTips = new BlockPos[4];
				int minX = Integer.MAX_VALUE;
				int minZ = Integer.MAX_VALUE;
				int maxX = Integer.MIN_VALUE;
				int maxZ = Integer.MIN_VALUE;
				for (int j = 0; j < 4; j++) {
					turt2.set(turt);
					int branchLength = (random.nextInt(1+(r/2)))+r;
					Direction dir = Direction.fromHorizontal(j);
					for (int k = 0; k < branchLength; k++) {
						turt2.move(dir);
						if (!canReplace(plan, world, turt2, false)) break;
						plan.put(turt2.toImmutable(), LOG.with(PillarBlock.AXIS, dir.getAxis()));
					}
					turt2.move(UP);
					if (canReplace(plan, world, turt2, true)) {
						plan.put(turt2.toImmutable(), LEAVES);
					}
					turt2.move(DOWN);
					turt2.move(dir);
					if (canReplace(plan, world, turt2, true)) {
						plan.put(turt2.toImmutable(), LEAVES);
					}
					branchTips[j] = turt2.toImmutable();
					minX = Math.min(minX, turt2.getX());
					minZ = Math.min(minZ, turt2.getZ());
					maxX = Math.max(maxX, turt2.getX());
					maxZ = Math.max(maxZ, turt2.getZ());
				}
				
				int ofsX = -minX;
				int ofsZ = -minZ;
				
				// these AWT path tools do *not* initialize the toolkit and are safe to use
				Path2D path = new Path2D.Float();
				Path2D path2 = new Path2D.Float();
				
				float lastX = -1;
				float lastZ = -1;
				float lastX2 = -1;
				float lastZ2 = -1;
				
				float upperInset = 1f;
				
				for (int j = 0; j <= 4; j++) {
					BlockPos tip = branchTips[j%4];
					Direction dir = Direction.fromHorizontal(j);
					
					float x = tip.getX()+ofsX;
					float z = tip.getZ()+ofsZ;
					
					float x2 = x-(dir.getOffsetX()*upperInset);
					float z2 = z-(dir.getOffsetZ()*upperInset);
					
					if (j != 0) {
						boolean ix = dir.getAxis() == Axis.X;
						path.quadTo(
							ix ? x : lastX, ix ? lastZ : z,
							x, z
						);
						path2.quadTo(
							ix ? x2 : lastX2, ix ? lastZ2 : z2,
							x2, z2
						);
					} else {
						path.moveTo(x, z);
						path2.moveTo(x2, z2);
					}
					
					lastX = x;
					lastZ = z;
					lastX2 = x2;
					lastZ2 = z2;
				}
//				System.out.println(path.getBounds2D());
//				try {
//					BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
//					Graphics2D g2d = img.createGraphics();
//					g2d.translate(8, 8);
//					g2d.scale(2, 2);
//					g2d.setColor(Color.RED);
//					g2d.draw(path);
//					g2d.setColor(Color.YELLOW);
//					g2d.draw(path2);
//					g2d.setColor(Color.GREEN);
//					for (BlockPos bp : branchTips) {
//						g2d.fillRect(bp.getX()+ofsX, bp.getZ()+ofsZ, 1, 1);
//					}
//					ImageIO.write(img, "PNG", new File("layer"+i+".png"));
//				} catch (Throwable t) {}
				
				for (BlockPos bp : BlockPos.iterate(minX, turt.getY(), minZ, maxX, turt.getY()+1, maxZ)) {
					Path2D p = (bp.getY() == turt.getY() ? path : path2);
					if (p.contains(bp.getX()+ofsX, bp.getZ()+ofsZ)) {
						if (canReplace(plan, world, bp, true)) {
							plan.put(bp.toImmutable(), LEAVES);
						}
					}
				}
				
				for (int j = 0; j < 2; j++) {
					turt.move(UP);
					if (!canReplace(plan, world, turt, false)) return false;
					plan.put(turt.toImmutable(), LOG);
				}
				turt.move(UP);
			}
		}
		
		for (Map.Entry<BlockPos, BlockState> en : plan.entrySet()) {
			world.setBlockState(en.getKey(), en.getValue().with(Properties.WATERLOGGED, world.getFluidState(en.getKey()).isIn(FluidTags.WATER)), 3);
		}
		
		return true;
	}

	private boolean canReplace(Map<BlockPos, BlockState> plan, WorldAccess world, BlockPos pos, boolean leaves) {
		BlockState bs = plan.getOrDefault(pos, world.getBlockState(pos));
		if (bs.isOf(YBlocks.SQUEEZE_LEAVES) || bs.isOf(YBlocks.SQUEEZE_SAPLING) || bs.isOf(YBlocks.SQUEEZED_LEAVES) || (!leaves && (bs.isOf(YBlocks.SQUEEZE_LOG) || bs.isOf(YBlocks.STRIPPED_SQUEEZE_LOG))))
			return true;
		return bs.isAir() || bs.getMaterial().isReplaceable() || bs.isOf(Blocks.KELP_PLANT);
	}

}
