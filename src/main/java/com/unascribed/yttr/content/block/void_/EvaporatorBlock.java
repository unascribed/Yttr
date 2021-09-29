package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.world.FilterNetworks;
import com.unascribed.yttr.world.FilterNetwork.NodeType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EvaporatorBlock extends Block {

	public static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 10, 16);
	
	public EvaporatorBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!state.isOf(oldState.getBlock())) {
			if (world instanceof ServerWorld) {
				FilterNetworks.get((ServerWorld)world).introduce(pos, NodeType.EVAPORATOR);
			}
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (!newState.isOf(state.getBlock())) {
			if (world instanceof ServerWorld) {
				FilterNetworks.get((ServerWorld)world).destroy(pos);
			}
		}
	}
	
}
