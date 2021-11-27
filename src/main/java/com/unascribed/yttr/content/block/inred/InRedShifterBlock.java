package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.inred.ShifterSelection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InRedShifterBlock extends InRedLogicTileBlock {
	public static final EnumProperty<ShifterSelection> SELECTION = EnumProperty.of("selection", ShifterSelection.class);

	public InRedShifterBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(SELECTION, ShifterSelection.LEFT));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InRedShifterBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof InRedShifterBlockEntity) {
			((InRedShifterBlockEntity)be).toggleSelection();
		}
		return ActionResult.SUCCESS;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(SELECTION);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side==state.get(FACING) || side==Direction.UP || side==Direction.DOWN) return 0;
//		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedShifterBlockEntity) {
			if (side==state.get(FACING).getOpposite()) {
				return ((InRedShifterBlockEntity)be).isActive() ? 16 : 0;
			} else if ((side==state.get(FACING).rotateYCounterclockwise() && state.get(SELECTION) == ShifterSelection.RIGHT)
					|| side==state.get(FACING).rotateYClockwise() && state.get(SELECTION)==ShifterSelection.LEFT) {
				return ((InRedShifterBlockEntity)be).isEject() ? 16 : 0;
			}
		}
		return 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof InRedShifterBlockEntity) {
				world.setBlockState(pos, state
						.with(SELECTION, ((InRedShifterBlockEntity)be).selection));
			}
		}
	}
}
