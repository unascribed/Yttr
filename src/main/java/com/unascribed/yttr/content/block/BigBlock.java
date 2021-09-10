package com.unascribed.yttr.content.block;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class BigBlock extends Block {

	public final IntProperty X, Y, Z;
	public final int xSize, ySize, zSize;
	
	public BigBlock(IntProperty x, IntProperty y, IntProperty z, Settings settings) {
		super(settings);
		this.xSize = Iterables.getLast(x.getValues())+1;
		this.ySize = Iterables.getLast(y.getValues())+1;
		this.zSize = Iterables.getLast(z.getValues())+1;
		X = x;
		Y = y;
		Z = z;
	}

	public @Nullable BlockState getExpectedNeighbor(BlockState state, Direction dir) {
		int x = state.get(X)+dir.getOffsetX();
		int y = state.get(Y)+dir.getOffsetY();
		int z = state.get(Z)+dir.getOffsetZ();
		if (x < 0 || y < 0 || z < 0) return null;
		if (x >= xSize || y >= ySize || z >= zSize) return null;
		return state.with(X, x).with(Y, y).with(Z, z);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		BlockState expected = getExpectedNeighbor(state, direction);
		if (expected == null) return state;
		if (newState != expected) {
			if (this instanceof Waterloggable && state.get(Properties.WATERLOGGED)) {
				return Blocks.WATER.getDefaultState();
			}
			return Blocks.AIR.getDefaultState();
		}
		return state;
	}

	private static final ThreadLocal<int[]> reentering = ThreadLocal.withInitial(() -> new int[1]);
	
	public static boolean handleSetBlockBreakingInfo(World w, int entityId, BlockPos pos, int progress) {
		int[] re = reentering.get();
		if (re[0] > 0) return false;
		BlockState bs = w.getBlockState(pos);
		if (bs.getBlock() instanceof BigBlock) {
			re[0]++;
			try {
				BigBlock b = (BigBlock)bs.getBlock();
				BlockPos origin = pos.add(-bs.get(b.X), -bs.get(b.Y), -bs.get(b.Z));
				int i = 1;
				for (int x = 0; x < b.xSize; x++) {
					for (int y = 0; y < b.ySize; y++) {
						for (int z = 0; z < b.zSize; z++) {
							w.setBlockBreakingInfo(entityId+(i*10000), origin.add(x, y, z), progress);
							i++;
						}
					}
				}
			} finally {
				re[0]--;
			}
			return true;
		}
		return false;
	}
	
}
