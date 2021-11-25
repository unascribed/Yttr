package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.InRedProvider;
import com.unascribed.yttr.inred.MultimeterProbeProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class InRedModuleBlock extends BlockWithEntity implements InRedProvider, MultimeterProbeProvider {

	public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);

	public InRedModuleBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return InRedLogic.isSideSolid(world, pos.down(), Direction.UP)
				//TODO: are these conditions even necessary?
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_SCAFFOLD
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_BLOCK;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BASE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public InRedDevice getDevice(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedModuleBlockEntity) {
			return ((InRedModuleBlockEntity) be).getComponent(inspectingFrom);
		}
		return null;
	}
}
