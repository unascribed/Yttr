package com.unascribed.yttr.inred;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class InRedHandler implements InRedDevice {
	public static final InRedDevice ALWAYS_OFF = () -> 0;

	public static final InRedDevice ALWAYS_MAX = () -> InRedLogic.MAX_SIGNAL;

	private int signalValue;
	private int nextSignalValue;
	private Runnable onChanged;

	public void listen(Runnable r) {
		this.onChanged = r;
	}

	@Override
	public int getSignalValue() {
		return signalValue;
	}

	public void setSignalValue(int val) {
		signalValue = val;
		onChanged();
	}

	public int getNextSignalValue() {
		return nextSignalValue;
	}

	public void setNextSignalValue(int val) {
		nextSignalValue = val;
		onChanged();
	}

	public int getEncoderValue() {
		return signalValue;
	}

	public void onChanged() {
		if (onChanged!=null) onChanged.run();
	}

	public NbtCompound serialize() {
		NbtCompound tag = new NbtCompound();
		tag.putInt("SignalValue", this.getSignalValue());
		tag.putInt("NextSignalValue", this.getNextSignalValue());
		return tag;
	}

	public void deserialize(NbtCompound tag) {
		setSignalValue(tag.getInt("SignalValue"));
		setNextSignalValue(tag.getInt("NextSignalValue"));
	}
}
