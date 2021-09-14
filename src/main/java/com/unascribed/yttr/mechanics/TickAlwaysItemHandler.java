package com.unascribed.yttr.mechanics;

import java.util.Set;

import com.unascribed.yttr.mixin.accessor.AccessorHorseBaseEntity;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class TickAlwaysItemHandler {

	public static void startServerWorldTick(ServerWorld world) {
		// TODO pick random chunks
		for (BlockEntity be : ImmutableList.copyOf(world.blockEntities)) {
			if (be instanceof Inventory && world.random.nextInt(40) == 0) {
				Inventory inv = (Inventory)be;
				for (int i = 0; i < inv.size(); i++) {
					ItemStack is = inv.getStack(i);
					if (is.getItem() instanceof TicksAlwaysItem) {
						((TicksAlwaysItem)is.getItem()).blockInventoryTick(is, world, be.getPos(), i);
						inv.setStack(i, is);
					}
				}
			}
		}
		for (Entity e : world.getEntitiesByType(null, Predicates.alwaysTrue())) {
			if (e instanceof PlayerEntity) {
				EnderChestInventory inv = ((PlayerEntity) e).getEnderChestInventory();
				for (int i = 0; i < inv.size(); i++) {
					ItemStack is = inv.getStack(i);
					if (is.getItem() instanceof TicksAlwaysItem) {
						((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, i, false);
						inv.setStack(i, is);
					}
				}
				continue;
			}
			if (e instanceof ItemEntity) {
				ItemStack is = ((ItemEntity) e).getStack();
				if (is.getItem() instanceof TicksAlwaysItem) {
					((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, 0, false);
					if (is.isEmpty()) e.remove();
				}
				continue;
			}
			if (e instanceof ItemFrameEntity) {
				ItemStack is = ((ItemFrameEntity) e).getHeldItemStack();
				if (is.getItem() instanceof TicksAlwaysItem) {
					((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, 0, false);
					if (is.isEmpty()) {
						((ItemFrameEntity) e).setHeldItemStack(ItemStack.EMPTY, true);
					}
				}
				continue;
			}
			if (world.random.nextInt(40) == 0) {
				Set<ItemStack> seen = Sets.newIdentityHashSet();
				if (e instanceof HorseBaseEntity) {
					SimpleInventory inv = ((AccessorHorseBaseEntity)e).yttr$getItems();
					for (int i = 0; i < inv.size(); i++) {
						ItemStack is = inv.getStack(i);
						if (is.getItem() instanceof TicksAlwaysItem && seen.add(is)) {
							((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, i, false);
							inv.setStack(i, is);
						}
					}
				}
				if (e instanceof LivingEntity) {
					for (EquipmentSlot slot : EquipmentSlot.values()) {
						ItemStack is = ((LivingEntity) e).getEquippedStack(slot);
						if (is.getItem() instanceof TicksAlwaysItem && seen.add(is)) {
							((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, slot.getEntitySlotId(), false);
							e.equipStack(slot, is);
						}
					}
				}
				if (e instanceof Inventory) {
					Inventory inv = (Inventory)e;
					for (int i = 0; i < inv.size(); i++) {
						ItemStack is = inv.getStack(i);
						if (is.getItem() instanceof TicksAlwaysItem && seen.add(is)) {
							((TicksAlwaysItem)is.getItem()).inventoryTick(is, world, e, i, false);
							inv.setStack(i, is);
						}
					}
				}
			}
		}
	}

}
