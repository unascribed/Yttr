package com.unascribed.yttr.block;

import com.unascribed.yttr.block.entity.VoidGeyserBlockEntity;
import com.unascribed.yttr.init.YFluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class VoidGeyserBlock extends Block implements BlockEntityProvider {

	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 1/16D, 1);
	
	public VoidGeyserBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return YFluids.VOID.getDefaultState();
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new VoidGeyserBlockEntity();
	}

}
