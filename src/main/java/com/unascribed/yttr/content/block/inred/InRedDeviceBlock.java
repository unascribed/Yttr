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

public abstract class InRedDeviceBlock extends BlockWithEntity implements InRedProvider, EncoderScannable, MultimeterProbeProvider {

	public InRedDeviceBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
}