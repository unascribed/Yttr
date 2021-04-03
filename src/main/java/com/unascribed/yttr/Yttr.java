package com.unascribed.yttr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCommands;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.init.YWorldGen;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.mixin.accessor.AccessorHorseBaseEntity;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Yttr implements ModInitializer {
	
	public static final Map<Identifier, SoundEvent> craftingSounds = Maps.newHashMap();
	
	@Override
	public void onInitialize() {
		YBlocks.init();
		YBlockEntities.init();
		YItems.init();
		YSounds.init();
		YFluids.init();
		YStatusEffects.init();
		YRecipeSerializers.init();
		YWorldGen.init();
		YCommands.init();
		YTags.init();
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "rifle_mode"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player.getMainHandStack().getItem() instanceof RifleItem) {
				((RifleItem)player.getMainHandStack().getItem()).attack(player);
			}
		});
		
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			// TODO pick random chunks
			for (BlockEntity be : ImmutableList.copyOf(world.blockEntities)) {
				if (be instanceof Inventory && world.random.nextInt(40) == 0) {
					Inventory inv = (Inventory)be;
					for (int i = 0; i < inv.size(); i++) {
						ItemStack is = inv.getStack(i);
						if (is.getItem() == YItems.SNARE) {
							YItems.SNARE.blockInventoryTick(is, world, be.getPos(), i);
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
						if (is.getItem() == YItems.SNARE) {
							YItems.SNARE.inventoryTick(is, world, e, i, false);
							inv.setStack(i, is);
						}
					}
					continue;
				}
				if (e instanceof ItemEntity) {
					ItemStack is = ((ItemEntity) e).getStack();
					if (is.getItem() == YItems.SNARE) {
						YItems.SNARE.inventoryTick(is, world, e, 0, false);
						if (is.isEmpty()) e.remove();
					}
					continue;
				}
				if (e instanceof ItemFrameEntity) {
					ItemStack is = ((ItemFrameEntity) e).getHeldItemStack();
					if (is.getItem() == YItems.SNARE) {
						YItems.SNARE.inventoryTick(is, world, e, 0, false);
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
							if (is.getItem() == YItems.SNARE && seen.add(is)) {
								YItems.SNARE.inventoryTick(is, world, e, i, false);
								inv.setStack(i, is);
							}
						}
					}
					if (e instanceof LivingEntity) {
						for (EquipmentSlot slot : EquipmentSlot.values()) {
							ItemStack is = ((LivingEntity) e).getEquippedStack(slot);
							if (is.getItem() == YItems.SNARE && seen.add(is)) {
								YItems.SNARE.inventoryTick(is, world, e, slot.getEntitySlotId(), false);
								e.equipStack(slot, is);
							}
						}
					}
					if (e instanceof Inventory) {
						Inventory inv = (Inventory)e;
						for (int i = 0; i < inv.size(); i++) {
							ItemStack is = inv.getStack(i);
							if (is.getItem() == YItems.SNARE && seen.add(is)) {
								YItems.SNARE.inventoryTick(is, world, e, i, false);
								inv.setStack(i, is);
							}
						}
					}
				}
			}
		});
		
	}

	public static <T> void autoRegister(Registry<T> registry, Class<?> holder, Class<? super T> type) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					Registry.register(registry, "yttr:"+f.getName().toLowerCase(Locale.ROOT), (T)f.get(null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
