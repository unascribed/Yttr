package com.unascribed.yttr.block.void_;

import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.world.GeysersState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (world instanceof ServerWorld) {
			GeysersState.get((ServerWorld)world).removeGeyser(pos);
		}
	}

}
