package com.unascribed.yttr.content.block.big;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;

public class GiantsBlock extends BigBlock {

	public static final IntProperty X = IntProperty.of("x", 0, 3);
	public static final IntProperty Y = IntProperty.of("y", 0, 3);
	public static final IntProperty Z = IntProperty.of("z", 0, 3);
	
	public GiantsBlock(Settings s) {
		super(X, Y, Z, s);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(X, Y, Z);
	}
	
}
