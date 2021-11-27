package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class InRedOscillatorBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	private int refreshTicks;
	public int maxRefreshTicks = 4;
	private int sigToWrite;

	public InRedOscillatorBlockEntity() {
		super(YBlockEntities.INRED_OSCILLATOR);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedOscillatorBlock) {
				Direction back = state.get(InRedOscillatorBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				if (sig != signal.getSignalValue()) {
					//in and out signals are different, check if it's in the middle of a refresh cycle
					if (refreshTicks <= 0) {
						//refresh cycle ended, set signal and grab next signal
						signal.setNextSignalValue(sigToWrite);
						sigToWrite = sig;
						refreshTicks = maxRefreshTicks;
					} else {
						//in the middle of a cycle, keep at what it currently is
						signal.setNextSignalValue(signal.getSignalValue());
					}
					refreshTicks -= 2;
					sync();
				}
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void setDelay() {
		if (maxRefreshTicks >= 100) maxRefreshTicks = 100;
		if (maxRefreshTicks < 1) maxRefreshTicks = 1;
		refreshTicks = maxRefreshTicks;
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == YBlocks.INRED_OSCILLATOR) {
			Direction oscillatorFront = state.get(InRedOscillatorBlock.FACING);
			if (oscillatorFront == inspectingFrom) {
				return signal;
			} else if (oscillatorFront == inspectingFrom.getOpposite()) {
				return InRedHandler.ALWAYS_OFF;
			} else {
				return null;
			}
		}
		return InRedHandler.ALWAYS_OFF; //We can't tell what our front face is, so supply a dummy that's always-off.
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("tip.yttr.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
		sigToWrite = tag.getInt("NextSignal");
		refreshTicks = tag.getInt("CurrentRefresh");
		maxRefreshTicks = tag.getInt("MaxRefresh");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		tag.putInt("NextSignal", sigToWrite);
		tag.putInt("CurrentRefresh", refreshTicks);
		tag.putInt("MaxRefresh", maxRefreshTicks);
		return tag;
	}

}
