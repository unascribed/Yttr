package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YSounds;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class YttriumPressurePlateBlock extends AbstractPressurePlateBlock {

	public static final IntProperty POWER = Properties.POWER;
	private final int weight;

	public YttriumPressurePlateBlock(Settings settings, int weight) {
		super(FabricBlockSettings.copyOf(settings)
				.noCollision());
		setDefaultState(getDefaultState().with(POWER, 0));
		this.weight = weight;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		int itemAmount = 0;
		for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class, BOX.offset(pos), e -> true)) {
			itemAmount += item.getStack().getCount();
			if (itemAmount >= weight) {
				itemAmount = weight;
				break;
			}
		}
		return (itemAmount*15)/weight;
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
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWER);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWER, rsOut);
	}

	@Override
	protected int getTickRate() {
		return 10;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

}
