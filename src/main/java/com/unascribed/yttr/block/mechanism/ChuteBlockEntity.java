package com.unascribed.yttr.block.mechanism;

import com.unascribed.yttr.block.mechanism.ChuteBlock.Mode;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.base.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChuteBlockEntity extends BlockEntity implements SidedInventory, Tickable {

	public ChuteBlockEntity() {
		super(YBlockEntities.CHUTE);
	}
	
	@Override
	public void tick() {
		if (getMode() == Mode.TAKE) {
			Box box = new Box(pos.getX(), pos.getY()+1, pos.getZ(), pos.getX()+1, pos.getY()+1.25, pos.getZ()+1);
			if (!getCachedState().get(ChuteBlock.PLATED)) {
				box = box.expand(-4/16D, 0, -4/16D);
			}
			for (ItemEntity ent : world.getEntitiesByClass(ItemEntity.class, box, e -> true)) {
				ItemStack stack = ent.getStack();
				if (transfer(world, pos, Direction.DOWN, stack, false)) {
					ent.remove();
				}
			}
		}
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int size() {
		return getMode().isDroppy() ? 1 : 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (getMode().isDroppy()) {
			if (transfer(world, pos, Direction.DOWN, stack, false)) return;
		}
		// fall back to dropping at the top, we don't want to void anything
		dropItem(world, pos, stack, 1, 1);
	}
	
	public static boolean transfer(World world, BlockPos origin, Direction scanDir, ItemStack stack, boolean simulate) {
		BlockPos.Mutable mut = origin.mutableCopy();
		while (true) {
			mut.move(scanDir);
			if (mut.getY() < 0 || mut.getY() > world.getHeight()) break;
			BlockState there = world.getBlockState(mut);
			if (there.getBlock() == YBlocks.CHUTE) {
				Mode m = there.get(ChuteBlock.MODE);
				if (m.isClogged()) {
					return false;
				}
			} else {
				Inventory inv = HopperBlockEntity.getInventoryAt(world, mut);
				if (inv != null) {
					int tgt = findSlot(scanDir.getOpposite(), inv, stack);
					if (tgt != -1) {
						if (!simulate) {
							ItemStack thereStack = inv.getStack(tgt);
							if (thereStack.isEmpty()) {
								inv.setStack(tgt, stack);
							} else {
								ItemStack copy = inv.getStack(tgt).copy();
								copy.increment(stack.getCount());
								inv.setStack(tgt, copy);
							}
						}
						return true;
					}
					return false;
				} else if (there.getCollisionShape(world, mut).isEmpty()) {
					if (scanDir == Direction.DOWN) {
						if (!simulate) dropItem(world, mut, stack, 0.7, -1);
						return true;
					} else if (scanDir == Direction.UP) {
						if (!simulate) dropItem(world, mut, stack, 0, 1);
						return true;
					}
				} else {
					Direction redir = ChuteBlock.getStairRedirection(scanDir, there);
					if (redir != null) {
						if (!simulate) {
							double x = mut.getX()+0.5;
							double y = mut.getY();
							double z = mut.getZ()+0.5;
							if (scanDir == Direction.DOWN) {
								y += 0.5;
							}
							x += redir.getOffsetX()*0.2;
							z += redir.getOffsetZ()*0.2;
							ItemEntity ent = new ItemEntity(world, x, y, z, stack);
							double velX = redir.getOffsetX()*0.2;
							double velY = scanDir == Direction.DOWN && world.getFluidState(mut).isIn(FluidTags.WATER) ? -0.2 : 0;
							double velZ = redir.getOffsetZ()*0.2;
							ent.setVelocity(velX, velY, velZ);
							world.spawnEntity(ent);
						}
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	private static void dropItem(World world, BlockPos pos, ItemStack stack, double yOfs, int ySig) {
		ItemEntity ent = new ItemEntity(world, pos.getX()+0.5, pos.getY()+yOfs, pos.getZ()+0.5, stack);
		ent.setVelocity(0, ySig < 0 && world.getFluidState(pos).isIn(FluidTags.WATER) ? -0.2 : 0, 0);
		world.spawnEntity(ent);
	}

	private static int findSlot(Direction side, Inventory inv, ItemStack stack) {
		int[] slots;
		if (inv instanceof SidedInventory) {
			slots = ((SidedInventory) inv).getAvailableSlots(side);
		} else {
			slots = new int[inv.size()];
			for (int i = 0; i < slots.length; i++) slots[i] = i;
		}
		for (int slot : slots) {
			ItemStack there = inv.getStack(slot);
			if ((there.isEmpty() || (there.isItemEqual(stack) && Objects.equal(there.getTag(), stack.getTag()))) && inv.isValid(slot, stack)
					&& (there.getCount()+stack.getCount()) <= there.getMaxCount()) {
				return slot;
			}
		}
		return -1;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return getMode().isDroppy() ? new int[] {0} : new int[] {};
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (getMode().isDroppy()) {
			return transfer(world, pos, Direction.DOWN, stack, true);
		}
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}
	
	public Mode getMode() {
		return getCachedState().get(ChuteBlock.MODE);
	}

}
