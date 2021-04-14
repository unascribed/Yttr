package com.unascribed.yttr.block.device;

import java.util.Random;

import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.mechanics.rifle.Shootable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PowerMeterBlock extends HorizontalFacingBlock implements BlockEntityProvider, Shootable {

	public PowerMeterBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new PowerMeterBlockEntity();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0, 0, 0, 1, 7/16D, 1);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getCollisionShape(state, world, pos, context);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be != null) {
			be.onSyncedBlockEvent(type, data);
		}
		return true;
	}
	
	
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.updateNeighborsAlways(pos, this);
		if (getWeakRedstonePower(state, world, pos, null) > 0) {
			world.getBlockTickScheduler().schedule(pos, this, 20);
		}
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof PowerMeterBlockEntity) {
			if (System.currentTimeMillis()-((PowerMeterBlockEntity) be).readoutTime > 4500) return 0;
			int readout = ((PowerMeterBlockEntity)be).readout;
			if (readout == 650) return 15;
			return MathHelper.ceil((readout/650f)*14);
		}
		return 0;
	}

	@Override
	public boolean onShotByRifle(World world, BlockState bs, LivingEntity user, RifleMode mode, float power, BlockPos pos, BlockHitResult bhr) {
		if (mode == RifleMode.DAMAGE && bhr.getSide() == Direction.UP || bhr.getSide() == bs.get(PowerMeterBlock.FACING)) {
			BlockEntity be = user.world.getBlockEntity(bhr.getBlockPos());
			if (be instanceof PowerMeterBlockEntity) {
				((PowerMeterBlockEntity)be).sendReadout((int)(power*500));
			}
		}
		return true;
	}
	
}
