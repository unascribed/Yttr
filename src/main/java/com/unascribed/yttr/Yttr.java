package com.unascribed.yttr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCommands;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.init.YScreenTypes;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.init.YWorldGen;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.mixin.accessor.AccessorHorseBaseEntity;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.netty.buffer.Unpooled;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Yttr implements ModInitializer {
	
	public static final int DIVING_BLOCKS_PER_TICK = 4;
	
	public static final Map<Identifier, SoundEvent> craftingSounds = Maps.newHashMap();
	
	@Override
	public void onInitialize() {
		YBlocks.init();
		YBlockEntities.init();
		YItems.init();
		YSounds.init();
		YFluids.init();
		YStatusEffects.init();
		YRecipeTypes.init();
		YRecipeSerializers.init();
		YWorldGen.init();
		YCommands.init();
		YTags.init();
		YScreenTypes.init();
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "attack"), (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				if (player != null && player.getMainHandStack().getItem() instanceof Attackable) {
					((Attackable)player.getMainHandStack().getItem()).attack(player);
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_pos"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player instanceof DiverPlayer) {
				int x = buf.readInt();
				int y = buf.readInt();
				int z = buf.readInt();
				server.execute(() -> {
					if (((DiverPlayer)player).yttr$isDiving()) {
						
					}
				});
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

	public static ListTag serializeInv(Inventory inv) {
		ListTag out = new ListTag();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (!is.isEmpty()) {
				CompoundTag c = is.toTag(new CompoundTag());
				c.putInt("Slot", i);
				out.add(c);
			}
		}
		return out;
	}
	
	public static void deserializeInv(ListTag tag, Inventory inv) {
		inv.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag c = tag.getCompound(i);
			inv.setStack(c.getInt("Slot"), ItemStack.fromTag(c));
		}
	}

	public static boolean isWearingFullSuit(Entity entity) {
		if (!(entity instanceof LivingEntity)) return false;
		LivingEntity le = (LivingEntity)entity;
		for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
			if (!(le.getEquippedStack(slot).getItem() instanceof SuitArmorItem)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isStandingOnDivingPlate(Entity e) {
		return e.isOnGround() && e.world.getBlockState(e.getBlockPos().down()).isOf(YBlocks.DIVING_PLATE);
	}

	public static void syncDive(ServerPlayerEntity p) {
		if (!(p instanceof DiverPlayer)) return;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		GeysersState gs = GeysersState.get(p.getServerWorld());
		for (UUID u : ((DiverPlayer)p).yttr$getKnownGeysers()) {
			Geyser g = gs.getGeyser(u);
			if (g != null) {
				g.write(buf);
			}
		}
		ServerPlayNetworking.send(p, new Identifier("yttr", "dive"), buf);
	}

	
}
