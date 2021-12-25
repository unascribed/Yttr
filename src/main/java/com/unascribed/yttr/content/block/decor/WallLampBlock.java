package com.unascribed.yttr.content.block.decor;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class WallLampBlock extends LampBlock implements Waterloggable {

	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	private final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Direction.class);
	
	public WallLampBlock(Settings settings, int baseSize, int baseHeight, int lightSize, int lightHeight) {
		super(settings);
		int lightHrz = (16-lightSize)/2;
		int baseHrz = (16-baseSize)/2;
		SHAPES.put(Direction.DOWN, VoxelShapes.union(
				createCuboidShape(baseHrz, 0, baseHrz, 16-baseHrz, baseHeight, 16-baseHrz),
				createCuboidShape(lightHrz, baseHeight, lightHrz, 16-lightHrz, baseHeight+lightHeight, 16-lightHrz)));
		SHAPES.put(Direction.UP, VoxelShapes.union(
				createCuboidShape(baseHrz, 16, baseHrz, 16-baseHrz, 16-baseHeight, 16-baseHrz),
				createCuboidShape(lightHrz, 16-baseHeight, lightHrz, 16-lightHrz, 16-baseHeight-lightHeight, 16-lightHrz)));
		
		SHAPES.put(Direction.SOUTH, VoxelShapes.union(
				createCuboidShape(baseHrz, baseHrz, 16, 16-baseHrz, 16-baseHrz, 16-baseHeight),
				createCuboidShape(lightHrz, lightHrz, 16-baseHeight, 16-lightHrz, 16-lightHrz, 16-baseHeight-lightHeight)));
		SHAPES.put(Direction.NORTH, VoxelShapes.union(
				createCuboidShape(baseHrz, baseHrz, 0, 16-baseHrz, 16-baseHrz, baseHeight),
				createCuboidShape(lightHrz, lightHrz, baseHeight, 16-lightHrz, 16-lightHrz, 2+lightHeight)));
		
		SHAPES.put(Direction.EAST, VoxelShapes.union(
				createCuboidShape(16, baseHrz, baseHrz, 16-baseHeight, 16-baseHrz, 16-baseHrz),
				createCuboidShape(16-baseHeight, lightHrz, lightHrz, 16-baseHeight-lightHeight, 16-lightHrz, 16-lightHrz)));
		SHAPES.put(Direction.WEST, VoxelShapes.union(
				createCuboidShape(0, baseHrz, baseHrz, baseHeight, 16-baseHrz, 16-baseHrz),
				createCuboidShape(baseHeight, lightHrz, lightHrz, baseHeight+lightHeight, 16-lightHrz, 16-lightHrz)));
		
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES.getOrDefault(state.get(FACING), SHAPES.get(Direction.DOWN));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING, WATERLOGGED);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getSide().getOpposite()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isIn(FluidTags.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
