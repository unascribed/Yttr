package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.EncoderScannable;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class InRedEncoderBlockEntity extends InRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();

	public InRedEncoderBlockEntity() {
		super(YBlockEntities.INRED_ENCODER);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedEncoderBlock) {
				Direction back = state.get(InRedEncoderBlock.FACING).getOpposite();
				BlockPos backPos = this.getPos().offset(back);
				int resBack = encodeSignal(backPos, back);
				if (resBack > 0) {
					signal.setNextSignalValue(resBack);
					markDirty();
				} else {
					int resBackTwo = encodeSignal(backPos.offset(back), back);
					if (resBackTwo > 0) {
						signal.setNextSignalValue(resBackTwo);
						markDirty();
						// can't find anything else, so check for redstone/inred signal
					} else {
						// redstone first so inred's redstone-catching doesn't override it
						if (!InRedLogic.checkCandidacy(world, backPos, back)) {
							int sigBack = world.getEmittedRedstonePower(backPos, back);
							signal.setNextSignalValue(sigBack);
						} else {
							signal.setNextSignalValue(InRedLogic.findIRValue(world, pos, back));
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

	private int encodeSignal(BlockPos pos, Direction from) {
		BlockState quantify = world.getBlockState(pos);
		// check for the encoder API
		if (quantify.getBlock() instanceof EncoderScannable) {
			return ((EncoderScannable) quantify.getBlock()).getEncoderValue(world, pos, quantify, from.getOpposite());
			// no simple encoder API, so check for an InventoryProvider
		} else if (quantify.getBlock() instanceof InventoryProvider) {
			SidedInventory inv = ((InventoryProvider)quantify.getBlock()).getInventory(quantify, this.world, pos);
			int stacksChecked = 0;
			float fillPercentage = 0f;
			for (int i = 0; i < (inv.size()); i++) {
				ItemStack stack = inv.getStack(i);
				if (!stack.isEmpty()) {
					fillPercentage += (float) stack.getCount() / (float) Math.min(inv.getMaxCountPerStack(), stack.getMaxCount());
					stacksChecked++;
				}
			}
			fillPercentage /= (float) inv.size();
			return MathHelper.floor(fillPercentage * 62.0F) + (stacksChecked > 0 ? 1 : 0);
			//no InventoryProvider, so scan the BE
		} else if (world.getBlockEntity(pos) != null) {
			BlockEntity be = world.getBlockEntity(pos);
			// check for inventories on the BE, make sure we only move on if we don't find any
			if (be instanceof Inventory) {
				Inventory inv = (Inventory) be;
				int stacksChecked = 0;
				float fillPercentage = 0f;
				for (int i = 0; i < (inv.size()); i++) {
					ItemStack stack = inv.getStack(i);
					if (!stack.isEmpty()) {
						fillPercentage += (float) stack.getCount() / (float) Math.min(inv.getMaxCountPerStack(), stack.getMaxCount());
						stacksChecked++;
					}
				}
				fillPercentage /= (float) inv.size();
				return MathHelper.floor(fillPercentage * 62.0F) + (stacksChecked > 0 ? 1 : 0);
			}
			// check for a vanilla comparator interface
		} else if (quantify.hasComparatorOutput()) {
			signal.setNextSignalValue(4 * quantify.getComparatorOutput(world, pos));
		}
		return 0;
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return  signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_ENCODER) {
			Direction encoderFront = state.get(InRedEncoderBlock.FACING);
			if (encoderFront == inspectingFrom) {
				return  signal;
			} else if (encoderFront == inspectingFrom.getOpposite()) {
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
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound tag = super.writeNbt(nbt);
		tag.put("Signal", signal.serialize());
		return tag;
	}

}
