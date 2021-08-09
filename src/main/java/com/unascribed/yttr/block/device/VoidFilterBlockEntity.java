package com.unascribed.yttr.block.device;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class VoidFilterBlockEntity extends BlockEntity implements Tickable, DelegatingInventory, SideyInventory {
	
	public static final class OutputEntry {
		public final Item item;
		public final float chance;
		
		public OutputEntry(Item item, double chance) {
			this.item = item;
			this.chance = (float)chance;
		}
	}
	
	public static final int TICKS_PER_TOCK = 100;
	public static final int TOCKS_PER_OP = (20*60)/TICKS_PER_TOCK;
	
	public static final ImmutableList<OutputEntry> OUTPUTS = ImmutableList.of(
				new OutputEntry(   YItems.ULTRAPURE_SILICA, 5.500),
				new OutputEntry(   YItems.ULTRAPURE_CARBON, 3.000),
				new OutputEntry( YItems.ULTRAPURE_CINNABAR, 2.000),
				new OutputEntry(     YItems.ULTRAPURE_IRON, 1.500),
				new OutputEntry( YItems.ULTRAPURE_LAZURITE, 0.450),
				new OutputEntry(  YItems.ULTRAPURE_YTTRIUM, 0.300),
				new OutputEntry(YItems.ULTRAPURE_NEODYMIUM, 0.200),
				new OutputEntry(     YItems.ULTRAPURE_GOLD, 0.175),
				new OutputEntry(  YItems.ULTRAPURE_WOLFRAM, 0.025)
			);
	
	private static final Multiset<Item> statQueue = HashMultiset.create();
	
	private final SimpleInventory inv = new SimpleInventory(9);
	private int tockProgress = 0;
	private int opTocks = 0;
	private int maxOpTocks = TOCKS_PER_OP;
	private UUID owner;
	
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
		if (!getCachedState().get(VoidFilterBlock.ENABLED)) return;
		if (tockProgress++ >= TICKS_PER_TOCK) {
			tockProgress = 0;
		} else {
			return;
		}
		if (!world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER) && !world.getBlockState(pos.down()).isOf(YBlocks.DORMANT_VOID_GEYSER)) return;
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
				if (ThreadLocalRandom.current().nextFloat()*100 < oe.chance) {
					if (inv.addStack(new ItemStack(oe.item)).isEmpty()) {
						statQueue.add(oe.item);
					}
				}
			}
			if (!statQueue.isEmpty() && owner != null) {
				ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(owner);
				if (player != null) {
					for (Multiset.Entry<Item> en : statQueue.entrySet()) {
						player.getStatHandler().increaseStat(player, Stats.CRAFTED.getOrCreateStat(en.getElement()), en.getCount());
					}
					statQueue.clear();
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
		if (owner != null) tag.putUuid("Owner", owner);
		CompoundTag statQTag = new CompoundTag();
		for (Multiset.Entry<Item> en : statQueue.entrySet()) {
			statQTag.putInt(Registry.ITEM.getId(en.getElement()).toString(), en.getCount());
		}
		tag.put("StatQueue", statQTag);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		opTocks = tag.getInt("OpTocks");
		owner = tag.containsUuid("Owner") ? tag.getUuid("Owner") : null;
		statQueue.clear();
		CompoundTag statQTag = tag.getCompound("StatQueue");
		for (String key : statQTag.getKeys()) {
			Item i = Registry.ITEM.getOrEmpty(Identifier.tryParse(key)).orElse(null);
			if (i != null) {
				statQueue.add(i, statQTag.getInt(key));
			}
		}
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
	
	@Override
	public void onOpen(PlayerEntity player) {
		DelegatingInventory.super.onOpen(player);
		if (owner == null) {
			owner = player.getUuid();
			markDirty();
		}
	}

}
