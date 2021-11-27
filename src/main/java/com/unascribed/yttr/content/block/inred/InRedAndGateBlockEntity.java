package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.inred.InactiveSelection;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class InRedAndGateBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	public boolean booleanMode;
	private int valLeft;
	private int valBack;
	private int valRight;
	public InactiveSelection inactive = InactiveSelection.NONE;

	public InRedAndGateBlockEntity() {
		super(YBlockEntities.INRED_AND_GATE);
	}

	@Override
	public void tick() {
		//TODO: do we need the firstTick stuff still?
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedAndGateBlock) {
				Direction left = state.get(InRedAndGateBlock.FACING).rotateYCounterclockwise();
				Direction right = state.get(InRedAndGateBlock.FACING).rotateYClockwise();
				Direction back = state.get(InRedAndGateBlock.FACING).getOpposite();
				int sigLeft = InRedLogic.findIRValue(world, pos, left);
				int sigRight = InRedLogic.findIRValue(world, pos, right);
				int sigBack = InRedLogic.findIRValue(world, pos, back);
				List<Integer> signals = new ArrayList<>();

				valLeft = sigLeft;
				valRight = sigRight;
				valBack = sigBack;
				int result = 0b11_1111; //63

				if (!booleanMode) {
					switch (inactive) {
						case LEFT:
							signals.add(sigBack);
							signals.add(sigRight);
							break;
						case BACK:
							signals.add(sigLeft);
							signals.add(sigRight);
							break;
						case RIGHT:
							signals.add(sigLeft);
							signals.add(sigBack);
							break;
						case NONE:
							signals.add(sigLeft);
							signals.add(sigBack);
							signals.add(sigRight);
					}

					for (int signal : signals) {
						// if any input added to signal is 0b00_0000, will result in no output
						result &= signal;
					}
				} else {
					switch (inactive) {
						case LEFT:
							result = (sigBack > 0 && sigRight > 0)? 1 : 0;
							break;
						case BACK:
							result = (sigLeft > 0 && sigRight > 0)? 1 : 0;
							break;
						case RIGHT:
							result = (sigLeft > 0 && sigBack > 0)? 1 : 0;
							break;
						case NONE:
							result = (sigLeft > 0 && sigBack > 0 && sigRight > 0)? 1 : 0;
					}
				}

				signal.setNextSignalValue(result);
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
		sync();
	}

	public void toggleInactive(InactiveSelection newInactive) {
		if (inactive == newInactive) {
			inactive = InactiveSelection.NONE;
		} else {
			inactive = newInactive;
		}
		world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.45f);
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (inspectingFrom == Direction.DOWN || inspectingFrom == Direction.UP) return null;
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_AND_GATE) {
			Direction andGateFront = state.get(InRedAndGateBlock.FACING);
			if (andGateFront == inspectingFrom) {
				return signal;
			} else if (andGateFront == inspectingFrom.getOpposite()) {
				return InRedHandler.ALWAYS_OFF;
			} else if (andGateFront == inspectingFrom.rotateYCounterclockwise()) {
				return InRedHandler.ALWAYS_OFF;
			} else if (andGateFront == inspectingFrom.rotateYClockwise()) {
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
		return valLeft != 0;
	}

	public boolean isBackActive() {
		return valBack != 0;
	}

	public boolean isRightActive() {
		return valRight != 0;
	}

	@Override
	public Text getProbeMessage() {
		return new TranslatableText("msg.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag) {
		super.readNbt(state, tag);
		if (tag.contains("Signal", NbtType.COMPOUND)) signal.deserialize(tag.getCompound("Signal"));
		booleanMode = tag.getBoolean("BooleanMode");
		valLeft = tag.getInt("Left");
		valBack = tag.getInt("Back");
		valRight = tag.getInt("Right");
		inactive = InactiveSelection.forName(tag.getString("Inactive"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound compound) {
		NbtCompound tag = super.writeNbt(compound);
		tag.put("Signal", signal.serialize());
		tag.putBoolean("BooleanMode", booleanMode);
		tag.putInt("Left", valLeft);
		tag.putInt("Back", valBack);
		tag.putInt("Right", valRight);
		tag.putString("Inactive", inactive.asString());
		return tag;
	}

}
