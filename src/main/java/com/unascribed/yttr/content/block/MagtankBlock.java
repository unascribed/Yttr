package com.unascribed.yttr.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;

public class MagtankBlock extends BigBlock {

	public static final IntProperty X = IntProperty.of("x", 0, 1);
	public static final IntProperty Y = IntProperty.of("y", 0, 2);
	public static final IntProperty Z = IntProperty.of("z", 0, 1);
	
	public MagtankBlock(Settings s) {
		super(X, Y, Z, s);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(X, Y, Z);
	}
	
}
