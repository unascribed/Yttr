package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class InRedXorGateBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	private int valLeft;
	private int valRight;
	public boolean booleanMode;

	public InRedXorGateBlockEntity() {
		super(YBlockEntities.INRED_XOR_GATE);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedXorGateBlock) {
				Direction left = state.get(InRedXorGateBlock.FACING).rotateYCounterclockwise();
				Direction right = state.get(InRedXorGateBlock.FACING).rotateYClockwise();
				int sigLeft = InRedLogic.findIRValue(world, pos, left);
				int sigRight = InRedLogic.findIRValue(world, pos, right);
				valLeft = sigLeft;
				valRight = sigRight;
				if (!booleanMode) {
					signal.setNextSignalValue(sigLeft ^ sigRight);
				} else {
					if (sigLeft > 0 && sigRight == 0) {
						signal.setNextSignalValue(1);
					} else if (sigLeft == 0 && sigRight > 0) {
						signal.setNextSignalValue(1);
					} else {
						signal.setNextSignalValue(0);
					}
				}
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void toggleBooleanMode() {
		if (booleanMode) {
			booleanMode = false;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.5f);
		} else {
			booleanMode = true;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.55f);
		}
		world.setBlockState(pos, getCachedState().with(InRedXorGateBlock.BOOLEAN_MODE, booleanMode));
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (inspectingFrom == Direction.DOWN || inspectingFrom == Direction.UP) return null;
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return  signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_XOR_GATE) {
			Direction xorGateFront = state.get(InRedXorGateBlock.FACING);
			if (xorGateFront == inspectingFrom) {
				return  signal;
			} else if (xorGateFront == inspectingFrom.rotateYCounterclockwise()) {
				return InRedHandler.ALWAYS_OFF;
			} else if (xorGateFront == inspectingFrom.rotateYClockwise()) {
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

	public boolean isLeftActive() {
		return valLeft!=0;
	}

	public boolean isRightActive() {
		return valRight!=0;
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("tip.yttr.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
		booleanMode = tag.getBoolean("BooleanMode");
		valLeft = tag.getInt("Left");
		valRight = tag.getInt("Right");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		tag.putBoolean("BooleanMode", booleanMode);
		tag.putInt("Left", valLeft);
		tag.putInt("Right", valRight);
		return tag;
	}

}
