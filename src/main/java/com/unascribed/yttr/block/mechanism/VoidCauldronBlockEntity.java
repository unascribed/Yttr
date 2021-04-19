package com.unascribed.yttr.block.mechanism;

import com.unascribed.yttr.block.void_.VoidFluidBlock;
import com.unascribed.yttr.init.YBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class VoidCauldronBlockEntity extends BlockEntity implements Inventory {

	public VoidCauldronBlockEntity() {
		super(YBlockEntities.VOID_CAULDRON);
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int size() {
		return 1;
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
		if (!stack.isEmpty()) {
			if (world instanceof ServerWorld) {
				((ServerWorld)world).spawnParticles(VoidFluidBlock.BLACK_DUST, pos.getX()+0.5, pos.getY()+0.9, pos.getZ()+0.5, 10, 0.15, 0.1, 0.15, 0.5);
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}
	
	

}
