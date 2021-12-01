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

public class InRedTransistorBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();

	public InRedTransistorBlockEntity() {
		super(YBlockEntities.INRED_TRANSISTOR);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedTransistorBlock) {
				Direction back = state.get(InRedTransistorBlock.FACING).getOpposite();
				Direction left = state.get(InRedTransistorBlock.FACING).rotateYCounterclockwise();
				Direction right = state.get(InRedTransistorBlock.FACING).rotateYClockwise();
				int sigBack = InRedLogic.findIRValue(world, pos, back);
				int sigLeft = InRedLogic.findIRValue(world, pos, left);
				int sigRight = InRedLogic.findIRValue(world, pos, right);
				if (sigBack > 0 && (sigLeft > 0 || sigRight > 0)) signal.setNextSignalValue(sigBack);
				else signal.setNextSignalValue(0);
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (inspectingFrom == Direction.DOWN || inspectingFrom == Direction.UP) return null;
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return signal;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == YBlocks.INRED_TRANSISTOR) {
			Direction transistorFront = state.get(InRedTransistorBlock.FACING);
			if (transistorFront == inspectingFrom) {
				return signal;
			} else if (transistorFront == inspectingFrom.getOpposite()) {
				return InRedHandler.ALWAYS_OFF;
			} else if (transistorFront == inspectingFrom.rotateYCounterclockwise()) {
				return InRedHandler.ALWAYS_OFF;
			} else if (transistorFront == inspectingFrom.rotateYClockwise()) {
				return InRedHandler.ALWAYS_OFF;
			} else {
				return null;
			}
		}
		return InRedHandler.ALWAYS_OFF; //We can't tell what our front face is, so supply a dummy that's always-off.
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("tip.yttr.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		return tag;
	}

}
