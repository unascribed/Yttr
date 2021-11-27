package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.ShifterSelection;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class InRedShifterBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	private InRedHandler eject = new InRedHandler();
	public ShifterSelection selection = ShifterSelection.LEFT;

	public InRedShifterBlockEntity() {
		super(YBlockEntities.INRED_SHIFTER);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedShifterBlock) {
				Direction back = state.get(InRedShifterBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				int ej = 0;

				if (selection == ShifterSelection.LEFT) {
					ej = (sig & 0b10_0000);
					ej = (ej != 0) ? 1 : 0;
					sig <<= 1;
					sig &= 0b011_1111;
				} else {
					ej = (sig & 0b00_0001);
					ej = (ej != 0) ? 1 : 0;
					sig >>>= 1;
					sig &= 0b011_1111;
				}

				signal.setNextSignalValue(sig);
				eject.setNextSignalValue(ej);
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			eject.setSignalValue(eject.getNextSignalValue());
			sync();
		}
	}

	public void toggleSelection() {
		if (selection == ShifterSelection.LEFT) {
			selection = ShifterSelection.RIGHT;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.55f);
		} else {
			selection = ShifterSelection.LEFT;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.55f);
		}
		eject.setNextSignalValue(0);
		eject.setSignalValue(0);
		world.setBlockState(pos, getCachedState().with(InRedShifterBlock.SELECTION, selection));
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (inspectingFrom == Direction.DOWN || inspectingFrom == Direction.UP) return null;
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return  signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_SHIFTER) {
			Direction shifterFront = state.get(InRedShifterBlock.FACING);
			if (shifterFront == inspectingFrom) {
				return signal;
			} else if (shifterFront == inspectingFrom.rotateYClockwise()) {
				if (selection == ShifterSelection.LEFT) return eject;
				else return InRedHandler.ALWAYS_OFF;
			} else if (shifterFront == inspectingFrom.rotateYCounterclockwise()) {
				if (selection == ShifterSelection.RIGHT) return eject;
				else return InRedHandler.ALWAYS_OFF;
			} else if (shifterFront == inspectingFrom.getOpposite()) {
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

	public boolean isEject() {
		return eject.getSignalValue() != 0;
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("tip.yttr.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
		if (tag.contains("Eject")) eject.deserialize(tag.getCompound("Eject"));
		selection = ShifterSelection.forName(tag.getString("Selection"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		tag.put("Eject", signal.serialize());
		tag.putString("Selection", selection.toString());
		return tag;
	}

}
