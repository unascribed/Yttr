package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YSounds;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CuprosteelPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public CuprosteelPressurePlateBlock(Settings settings) {
		super(FabricBlockSettings.copyOf(settings)
				.noCollision());
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWERED, rsOut > 0);
	}

	@Override
	protected void playPressSound(WorldAccess world, BlockPos pos) {
		world.playSound(null, pos, YSounds.METAL_PLATE_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
	}

	@Override
	protected void playDepressSound(WorldAccess world, BlockPos pos) {
		world.playSound(null, pos, YSounds.METAL_PLATE_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		return world.getPlayers(TargetPredicate.DEFAULT, null, BOX.offset(pos)).isEmpty() ? 0 : 15;
	}

}
