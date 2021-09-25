package com.unascribed.yttr.content.block.decor;

import java.util.Collections;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class CleavedBlock extends Block implements BlockEntityProvider, BlockColorProvider, Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);
	
	public CleavedBlock(Settings settings) {
		super(settings.luminance(bs -> bs.get(LUMINANCE)));
		setDefaultState(getDefaultState().with(WATERLOGGED, false).with(LUMINANCE, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LUMINANCE);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new CleavedBlockEntity();
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
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
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getOpacity(world, pos);
		}
		return super.getOpacity(state, world, pos);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return MinecraftClient.getInstance().getBlockColors().getColor(((CleavedBlockEntity)be).getDonor(), world, pos, tintIndex);
		}
		return -1;
	}
	
}
