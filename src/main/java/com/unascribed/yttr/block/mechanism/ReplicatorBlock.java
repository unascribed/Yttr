package com.unascribed.yttr.block.mechanism;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ReplicatorBlock extends Block implements BlockEntityProvider {

	public ReplicatorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ReplicatorBlockEntity();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.CONSUME;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
			if (player.isCreative() || player.hasPermissionLevel(2) || player.getUuid().equals(rbe.owner)) {
				rbe.item = player.getStackInHand(hand).copy();
				rbe.sync();
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.FAIL;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		ItemStack stack = super.getPickStack(world, pos, state);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			stack.putSubTag("BlockEntityTag", be.toTag(new CompoundTag()));
		}
		return stack;
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			((ReplicatorBlockEntity)be).owner = placer.getUuid();
		}
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		// prevent sending of break particle event
	}

}
