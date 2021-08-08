package com.unascribed.yttr.block.device;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class VoidFilterBlockEntity extends BlockEntity implements Tickable, DelegatingInventory, SideyInventory {
	
	private static class OutputEntry {
		public final Item item;
		public final float chance;
		
		public OutputEntry(Item item, float chance) {
			this.item = item;
			this.chance = chance;
		}
	}
	
	public static final int TICKS_PER_TOCK = 100;
	public static final int TOCKS_PER_OP = (20*60)/TICKS_PER_TOCK;
	
	private static final ImmutableList<OutputEntry> OUTPUTS = ImmutableList.of(
				new OutputEntry(YItems.ULTRAPURE_SILICA, 0.5f),
				new OutputEntry(YItems.ULTRAPURE_CARBON, 0.25f+0.0055f),
				new OutputEntry(YItems.ULTRAPURE_CINNABAR, 0.25f),
				new OutputEntry(YItems.ULTRAPURE_IRON, 0.15f),
				new OutputEntry(YItems.ULTRAPURE_LAZURITE, 0.045f),
				new OutputEntry(YItems.ULTRAPURE_YTTRIUM, 0.03f),
				new OutputEntry(YItems.ULTRAPURE_NEODYMIUM, 0.02f),
				new OutputEntry(YItems.ULTRAPURE_GOLD, 0.015f),
				new OutputEntry(YItems.ULTRAPURE_WOLFRAM, 0.0001f)
			);
	
	private final SimpleInventory inv = new SimpleInventory(9);
	private int tockProgress = 0;
	private int opTocks = 0;
	private int maxOpTocks = TOCKS_PER_OP;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return (opTocks*TICKS_PER_TOCK)+tockProgress;
				case 1: return maxOpTocks*TICKS_PER_TOCK;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: opTocks = value/TICKS_PER_TOCK; break;
				case 1: maxOpTocks = value/TICKS_PER_TOCK; break;
			}

		}

		@Override
		public int size() {
			return 2;
		}
	};
	
	public VoidFilterBlockEntity() {
		super(YBlockEntities.VOID_FILTER);
		inv.addListener(i -> markDirty());
	}
	
	@Override
	public void tick() {
		if (world.isClient) return;
		if (tockProgress++ >= TICKS_PER_TOCK) {
			tockProgress = 0;
		} else {
			return;
		}
		if (!world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER)) return;
		if (!getCachedState().get(VoidFilterBlock.ENABLED)) return;
		boolean invFull = true;
		for (int i = 0; i < size(); i++) {
			if (getStack(i).isEmpty()) {
				invFull = false;
				break;
			}
		}
		if (!invFull && opTocks++ >= maxOpTocks) {
			opTocks = 0;
			for (OutputEntry oe : OUTPUTS) {
				if (ThreadLocalRandom.current().nextFloat()*10 < oe.chance) {
					inv.addStack(new ItemStack(oe.item));
				}
			}
		}
		markDirty();
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("OpTocks", opTocks);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		opTocks = tag.getInt("OpTocks");
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.getSquaredDistance(player.getPos(), false) < 5*5;
	}

	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

}
