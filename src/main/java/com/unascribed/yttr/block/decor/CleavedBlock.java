package com.unascribed.yttr.block.decor;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CleavedBlock extends Block implements BlockEntityProvider {

	public CleavedBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CleavedBlockEntity();
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity) be).getShape();
		}
		return super.getOutlineShape(state, world, pos, context);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		BlockEntity be = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getDroppedStacks(builder);
		}
		return Collections.emptyList();
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			world.syncWorldEvent(player, 2001, pos, getRawIdFromState(((CleavedBlockEntity) be).getDonor()));
		}
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().calcBlockBreakingDelta(player, world, pos);
		}
		return super.calcBlockBreakingDelta(state, player, world, pos);
	}
	
}
