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

public class InRedDiodeBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	private int mask = 0b11_1111;

	public InRedDiodeBlockEntity() {
		super(YBlockEntities.INRED_DIODE);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedDiodeBlock) {
				Direction back = state.get(InRedDiodeBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				signal.setNextSignalValue(sig & mask);
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void setMask(int bit) {
		mask ^= (1 << bit);
		world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.45f);
		world.setBlockState(pos, getCachedState().with(InRedDiodeBlock.BITS[bit], (mask & (1 << bit)) > 0));
		sync();
	}

	public int getMask() {
		return mask;
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return  signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_DIODE) {
			Direction diodeFront = state.get(InRedDiodeBlock.FACING);
			if (diodeFront == inspectingFrom) {
				return  signal;
			} else if (diodeFront == inspectingFrom.getOpposite()) {
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
		mask = tag.getInt("Mask");
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.putInt("Mask", mask);
		tag.put("Signal", signal.serialize());
		return tag;
	}

}
