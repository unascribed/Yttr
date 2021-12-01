package com.unascribed.yttr.inventory;

import com.unascribed.yttr.content.block.inred.InRedOscillatorBlockEntity;
import com.unascribed.yttr.init.YHandledScreens;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class InRedOscillatorScreenHandler extends ScreenHandler {
	private BlockPos pos;
	private BlockState state;
	private InRedOscillatorBlockEntity be;

	public InRedOscillatorScreenHandler(int syncId, PlayerInventory inv) {
		super(YHandledScreens.INRED_OSCILLATOR, syncId);
	}

	public InRedOscillatorScreenHandler(int syncId, BlockPos pos, PlayerEntity player) {
		super(YHandledScreens.INRED_OSCILLATOR, syncId);
		this.pos = pos;
		this.state = player.world.getBlockState(pos);
		this.be = (InRedOscillatorBlockEntity) player.world.getBlockEntity(pos);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public BlockPos getPos() {
		return pos;
	}

	public BlockState getState() {
		return state;
	}

	public InRedOscillatorBlockEntity getOscillator() {
		return be;
	}
}
