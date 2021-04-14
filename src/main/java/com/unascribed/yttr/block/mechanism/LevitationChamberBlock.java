package com.unascribed.yttr.block.mechanism;

import java.util.Locale;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LevitationChamberBlock extends Block implements BlockEntityProvider {

	public enum Obstruction implements StringIdentifiable {
		NONE,
		SOLID,
		CHUTE,
		;

		@Override
		public String asString() {
			return name().toLowerCase(Locale.ROOT);
		}
		
	}
	
	public static final EnumProperty<Obstruction> OBSTRUCTION = EnumProperty.of("obstruction", Obstruction.class);
	
	private static final VoxelShape SHAPE = VoxelShapes.union(
			VoxelShapes.cuboid(0, 0, 0, 16/16D, 10/16D, 16/16D),
			VoxelShapes.cuboid(2/16D, 10/16D, 2/16D, 14/16D, 16/16D, 14/16D)
		);
	
	public LevitationChamberBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(OBSTRUCTION);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (direction == Direction.UP) {
			if (newState.isOf(Blocks.CHEST) || (newState.isOpaque() && newState.isSideSolidFullSquare(world, posFrom, Direction.DOWN))) {
				return state.with(OBSTRUCTION, Obstruction.SOLID);
			} else if (newState.isOf(YBlocks.CHUTE)) {
				return state.with(OBSTRUCTION, Obstruction.CHUTE);
			} else {
				return state.with(OBSTRUCTION, Obstruction.NONE);
			}
		}
		return state;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof LevitationChamberBlockEntity) {
				player.openHandledScreen((LevitationChamberBlockEntity)blockEntity);
			}

			return ActionResult.CONSUME;
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos up = ctx.getBlockPos().up();
		return getStateForNeighborUpdate(getDefaultState(), Direction.UP, ctx.getWorld().getBlockState(up), ctx.getWorld(), ctx.getBlockPos(), up);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new LevitationChamberBlockEntity();
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

}
