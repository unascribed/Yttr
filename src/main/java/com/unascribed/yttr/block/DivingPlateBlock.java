package com.unascribed.yttr.block;

import com.unascribed.yttr.Voidloggable;
import com.unascribed.yttr.block.entity.VoidGeyserBlockEntity;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YTags;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class DivingPlateBlock extends Block implements Voidloggable {

	private static final VoxelShape SHAPE = createCuboidShape(0, 14, 0, 16, 16, 16);
	
	public DivingPlateBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(VOIDLOGGED, false));
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(VOIDLOGGED);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(VOIDLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isIn(YTags.Fluid.VOID));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(VOIDLOGGED) ? YFluids.VOID.getStill(false) : super.getFluidState(state);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getDrop(world, pos);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		dropStack(world, pos, getDrop(world, pos));
	}
	
	private ItemStack getDrop(BlockView world, BlockPos pos) {
		ItemStack stack = new ItemStack(this);
		if (world instanceof World) {
			VoidGeyserBlockEntity geyser = findClosestGeyser((World)world, pos);
			if (geyser != null) {
				stack.setCustomName(new LiteralText(geyser.getName()));
			}
		}
		return stack;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!itemStack.hasCustomName()) return;
		VoidGeyserBlockEntity geyser = findClosestGeyser(world, pos);
		if (geyser != null) {
			geyser.setName(itemStack.getName().asString());
			geyser.markDirty();
		}
	}

	public static VoidGeyserBlockEntity findClosestGeyser(World world, BlockPos pos) {
		int cX = pos.getX() >> 4;
		int cZ = pos.getZ() >> 4;
		double closestDist = Double.POSITIVE_INFINITY;
		VoidGeyserBlockEntity closest = null;
		for (int cXo = -1; cXo <= 1; cXo++) {
			for (int cZo = -1; cZo <= 1; cZo++) {
				WorldChunk c = world.getChunk(cX+cXo, cZ+cZo);
				for (BlockEntity be : c.getBlockEntities().values()) {
					if (be instanceof VoidGeyserBlockEntity) {
						double dist = be.getPos().getSquaredDistance(pos);
						if (dist < closestDist) {
							closestDist = dist;
							closest = (VoidGeyserBlockEntity)be;
						}
					}
				}
			}
		}
		return closest;
	}

}
