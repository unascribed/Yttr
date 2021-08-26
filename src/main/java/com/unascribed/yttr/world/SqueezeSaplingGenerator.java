package com.unascribed.yttr.world;

import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Random;

import com.unascribed.yttr.content.block.natural.SqueezeLogBlock;
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
		// make sure we're on valid soil
		if (!YBlocks.SQUEEZE_SAPLING.canPlaceAt(YBlocks.SQUEEZE_SAPLING.getDefaultState(), world, pos)) return false;
		Map<BlockPos, BlockState> plan = Maps.newLinkedHashMap();
		
		BlockPos.Mutable turt = pos.mutableCopy();
		BlockPos.Mutable turt2 = new BlockPos.Mutable();
		
		// both of these MUST be waterloggable due to the path applier at the end of the method setting waterlogged based on fluid presence
		BlockState LOG = YBlocks.SQUEEZE_LOG.getDefaultState().with(SqueezeLogBlock.ALIVE, true);
		BlockState LEAVES = YBlocks.SQUEEZE_LEAVES.getDefaultState().with(LeavesBlock.DISTANCE, 1);
		
		int baseStemHeight = random.nextInt(3)+2;
		int segments = random.nextInt(4)+3;

		// fill in the base stem for some extra height variation, since every segment is always 2 blocks apart
		for (int i = 0; i < baseStemHeight; i++) {
			if (!canReplace(plan, world, turt, false)) return false;
			plan.put(turt.toImmutable(), LOG);
			turt.move(UP);
		}
		
		for (int i = 0; i < segments; i++) {
			if (!canReplace(plan, world, turt, false)) return false;
			plan.put(turt.toImmutable(), LOG);
			if (i == segments-1) {
				// for the topmost segment, add a little spruce-y cap with 5 leaves like this
				//  #
				// #L#
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
				// draw 4 cardinal branches of random lengths
				for (int j = 0; j < 4; j++) {
					turt2.set(turt);
					int branchLength = (random.nextInt(1+(r/2)))+r;
					Direction dir = Direction.fromHorizontal(j);
					for (int k = 0; k < branchLength; k++) {
						turt2.move(dir);
						// we ran into something; truncate the branch here
						if (!canReplace(plan, world, turt2, false)) {
							turt2.move(dir, -1);
							break;
						}
						plan.put(turt2.toImmutable(), LOG.with(PillarBlock.AXIS, dir.getAxis()));
					}
					turt2.move(UP);
					if (canReplace(plan, world, turt2, true)) {
						// put leaves above the final log
						// #
						// L
						plan.put(turt2.toImmutable(), LEAVES);
					}
					turt2.move(DOWN);
					turt2.move(dir);
					if (canReplace(plan, world, turt2, true)) {
						// put leaves sticking out from the final log
						// #L
						plan.put(turt2.toImmutable(), LEAVES);
					}
					// store the location of the final leaf in our array
					branchTips[j] = turt2.toImmutable();
					minX = Math.min(minX, turt2.getX());
					minZ = Math.min(minZ, turt2.getZ());
					maxX = Math.max(maxX, turt2.getX());
					maxZ = Math.max(maxZ, turt2.getZ());
				}
				
				// AWT path stuff seems to act weird with negative or extremely large coordinates
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
				
				// here's the fun part: draw a quadratic line between each branch tip to fill in the leaves
				for (int j = 0; j <= 4; j++) {
					BlockPos tip = branchTips[j%4];
					Direction dir = Direction.fromHorizontal(j);
					
					float x = tip.getX()+ofsX;
					float z = tip.getZ()+ofsZ;
					
					float x2 = x-(dir.getOffsetX()*upperInset);
					float z2 = z-(dir.getOffsetZ()*upperInset);
					
					if (j != 0) {
						// last is initialized, draw a line
						boolean ix = dir.getAxis() == Axis.X;
						path.quadTo(
							ix ? x : lastX, ix ? lastZ : z,
							x, z
						);
						path2.quadTo(
							ix ? x2 : lastX2, ix ? lastZ2 : z2,
							x2, z2
						);
						// above confusing ternary is the "corner" of the two points, like this:
						// 1    ?
						//
						//      2
						// 1 is x,z; 2 is lastX,lastZ, ? is the resultant control point
						// this gives us a round-ish quadratic spline that does a nice job of filling in the space
					} else {
						// for the first point, last is not initialized, so just jump to this point to start our path
						path.moveTo(x, z);
						path2.moveTo(x2, z2);
					}
					
					lastX = x;
					lastZ = z;
					lastX2 = x2;
					lastZ2 = z2;
				}
				// debug image dumper
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
				
				// iterate over a cube covering the two paths we just created
				for (BlockPos bp : BlockPos.iterate(minX, turt.getY(), minZ, maxX, turt.getY()+1, maxZ)) {
					// the lower Y is path, the higher Y is path2
					Path2D p = (bp.getY() == turt.getY() ? path : path2);
					// test if this position is inside of the closed path's bounds
					if (p.contains(bp.getX()+ofsX, bp.getZ()+ofsZ)) {
						if (canReplace(plan, world, bp, true)) {
							// place leaves if we can
							plan.put(bp.toImmutable(), LEAVES);
						}
					}
				}
				
				// fill in the next two logs leading to our next segment
				for (int j = 0; j < 2; j++) {
					turt.move(UP);
					// bail if we're out of space
					if (!canReplace(plan, world, turt, false)) return false;
					plan.put(turt.toImmutable(), LOG);
				}
				turt.move(UP);
			}
		}
		
		// we now know we have enough space, so commit our plan to the world
		for (Map.Entry<BlockPos, BlockState> en : plan.entrySet()) {
			world.setBlockState(en.getKey(), en.getValue().with(Properties.WATERLOGGED, world.getFluidState(en.getKey()).isIn(FluidTags.WATER)), 3);
		}
		
		return true;
	}

	private boolean canReplace(Map<BlockPos, BlockState> plan, WorldAccess world, BlockPos pos, boolean leaves) {
		BlockState bs = plan.getOrDefault(pos, world.getBlockState(pos));
		// for leaves, we don't want to overwrite logs; any other squeeze-y block is fair game
		if (bs.isOf(YBlocks.SQUEEZE_LEAVES) || bs.isOf(YBlocks.SQUEEZE_SAPLING) || bs.isOf(YBlocks.SQUEEZED_LEAVES)
				|| (!leaves && (bs.isOf(YBlocks.SQUEEZE_LOG) || bs.isOf(YBlocks.STRIPPED_SQUEEZE_LOG))))
			return true;
		return bs.isAir() || bs.getMaterial().isReplaceable() || bs.isOf(Blocks.KELP_PLANT);
	}

}
