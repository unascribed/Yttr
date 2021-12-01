package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class InRedDemoCyclerBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();

	private boolean active;
	private boolean increasing;
	private int pauseTicks;

	public InRedDemoCyclerBlockEntity() {
		super(YBlockEntities.INRED_DEMO_CYCLER);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedDemoCyclerBlock) {
				if (active) {
					if (increasing) {
						if (signal.getSignalValue() < 63) {
							signal.setNextSignalValue(signal.getSignalValue() + 1);
						} else if (pauseTicks < 20) {
							signal.setNextSignalValue(63);
							pauseTicks++;
						} else {
							increasing = false;
							pauseTicks = 0;
						}
					} else {
						if (signal.getSignalValue() > 0) {
							signal.setNextSignalValue(signal.getSignalValue() - 1);
						} else {
							signal.setNextSignalValue(0);
							increasing = true;
							active = false;
						}
					}
				}
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
		}
	}

	public void activate() {
		active = true;
		pauseTicks = 0;
		increasing = true;
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		return signal;
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("msg.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
		active = tag.getBoolean("Active");
		increasing = tag.getBoolean("Increasing");
		pauseTicks = tag.getInt("Pause");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		tag.putBoolean("Active", active);
		tag.putBoolean("Increasing", increasing);
		tag.putInt("Pause", pauseTicks);
		return tag;
	}

}
