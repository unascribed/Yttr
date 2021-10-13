package com.unascribed.yttr.content.block.void_;

import java.util.Random;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.world.GeysersState;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		// so the client and server get the same result (assuming their clocks are synced within 5 seconds)
		Random consistent = new Random((System.currentTimeMillis()/5000L)+entity.getEntityId());
		entity.setVelocity(consistent.nextGaussian()/2, 1, consistent.nextGaussian()/2);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (world.getBlockState(pos.up()).isOf(YBlocks.VOID_FILTER)) {
			return YBlocks.DORMANT_VOID_GEYSER.getDefaultState();
		}
		return state;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (state.isOf(this) && !newState.isOf(this) && world instanceof ServerWorld) {
			GeysersState.get((ServerWorld)world).removeGeyser(pos);
		}
	}

}
