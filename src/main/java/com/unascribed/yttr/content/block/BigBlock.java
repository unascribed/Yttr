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
	
}
