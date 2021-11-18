package com.unascribed.yttr.content.block.natural;

import java.util.Random;

import com.unascribed.yttr.init.YBlocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SqueezeLogBlock extends PillarBlock {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
	
	public SqueezeLogBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false).with(ALIVE, false));
	}
	
	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(ALIVE) && state.get(WATERLOGGED);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos down = pos.down();
			BlockState below = world.getBlockState(down);
			if ((below.isAir() || below.getMaterial().isReplaceable()) && below.getFluidState().isIn(FluidTags.WATER)) {
				world.setBlockState(down, YBlocks.DELICACE.getDefaultState());
			}
		}
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (hasRandomTicks(state) && random.nextInt(8) == 0) {
			YBlocks.DELICACE.randomDisplayTick(state, world, pos, random);
		}
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem().isIn(FabricToolTags.AXES) && this != YBlocks.STRIPPED_SQUEEZE_LOG) {
			world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (!world.isClient) {
				world.setBlockState(pos, YBlocks.STRIPPED_SQUEEZE_LOG.getDefaultState()
						.with(AXIS, state.get(AXIS))
						.with(WATERLOGGED, state.get(WATERLOGGED)), 11);
				stack.damage(1, player, ((p) -> {
					p.sendToolBreakStatus(hand);
				}));
			}

			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED, ALIVE);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
		if (fluid.isIn(FluidTags.WATER)) state = state.with(WATERLOGGED, true);
		return state;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
