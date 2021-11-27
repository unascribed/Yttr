package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class InRedDeviceBlock extends BlockWithEntity implements InRedProvider, EncoderScannable, MultimeterProbeProvider, Waterloggable {
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public static final VoxelShape CLICK_BOOLEAN = Block.createCuboidShape( 6, 2.9,  3, 10, 4.1,  7);
	public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);

	public InRedDeviceBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return InRedLogic.isSideSolid(world, pos.down(), Direction.UP)
				//TODO: are these conditions even necessary?
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_SCAFFOLD
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_BLOCK;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BASE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public InRedDevice getDevice(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getDevice(inspectingFrom);
		}
		return null;
	}

	@Override
	public int getEncoderValue(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getEncoderValue(inspectingFrom);
		}
		return 0;
	}

	@Override
	public Text getProbeMessage(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getProbeMessage();
		}
		//TODO: better fallback message? this should never happen anyway lol
		return new TranslatableText("msg.inred.multimeter.block");
	}

	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}