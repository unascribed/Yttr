package com.unascribed.yttr.block.squeeze;

import java.util.Random;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SqueezedLeavesBlock extends SqueezeLeavesBlock implements BlockEntityProvider {

	public static final BooleanProperty SQUEEZING = BooleanProperty.of("squeezing");
	
	public SqueezedLeavesBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(SQUEEZING, false));
	}
	
	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(YBlocks.SQUEEZE_LEAVES);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!player.getStackInHand(hand).isEmpty()) return ActionResult.PASS;
		if (state.get(SQUEEZING)) return ActionResult.FAIL;
		if (world.getDimension().isUltrawarm()) return ActionResult.FAIL;
		if (!world.isClient) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof SqueezedLeavesBlockEntity) {
				if (!((SqueezedLeavesBlockEntity) be).finished) {
					world.setBlockState(pos, state.with(SQUEEZING, true));
					world.getBlockTickScheduler().schedule(pos, this, 4);
					((SqueezedLeavesBlockEntity)be).step();
				}
			}
		}
		return ActionResult.success(true);
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(SQUEEZING)) {
			world.setBlockState(pos, state.with(SQUEEZING, false));
		} else {
			super.scheduledTick(state, world, pos, random);
		}
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SqueezedLeavesBlockEntity) {
			SqueezedLeavesBlockEntity slbe = (SqueezedLeavesBlockEntity)be;
			if (slbe.decayTime++ > 8) {
				BlockState newState = YBlocks.SQUEEZE_LEAVES.getDefaultState();
				newState = newState.with(DISTANCE, state.get(DISTANCE))
						.with(PERSISTENT, state.get(PERSISTENT))
						.with(WATERLOGGED, state.get(WATERLOGGED));
				world.setBlockState(pos, newState);
				return;
			}
			slbe.markDirty();
		}
		super.randomTick(state, world, pos, random);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SQUEEZING);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SqueezedLeavesBlockEntity();
	}

}
