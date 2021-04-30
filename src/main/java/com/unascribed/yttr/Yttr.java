package com.unascribed.yttr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;

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
import com.unascribed.yttr.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixin.accessor.AccessorDispenserBlock;
import com.unascribed.yttr.mixin.accessor.AccessorHorseBaseEntity;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.util.Attackable;
import com.unascribed.yttr.util.EquipmentSlots;
import com.unascribed.yttr.util.math.Vec2i;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.base.Predicates;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
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
import net.minecraft.server.world.ServerWorld;
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
		
		DispenserBlock.registerBehavior(YItems.REPLICATOR, (pointer, stack) -> {
			ItemStack inside = ReplicatorBlockItem.getHeldItem(stack);
			Block b = pointer.getBlockState().getBlock();
			if (b instanceof AccessorDispenserBlock) {
				((AccessorDispenserBlock)b).yttr$getBehaviorForItem(inside).dispense(pointer, inside);
			}
			return stack;
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "attack"), (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				if (player != null && player.getMainHandStack().getItem() instanceof Attackable) {
					((Attackable)player.getMainHandStack().getItem()).attack(player);
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_pos"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player instanceof DiverPlayer) {
				DiverPlayer diver = (DiverPlayer)player;
				
				int x = buf.readInt();
				int z = buf.readInt();
				server.execute(() -> {
					if (diver.yttr$isDiving()) {
						int ticks = server.getTicks();
						int lastUpdate = diver.yttr$getLastDivePosUpdate();
						int diff = ticks-lastUpdate;
						diver.yttr$setLastDivePosUpdate(ticks);
						if (lastUpdate != 0 && diff < 4) {
							LogManager.getLogger("Yttr").warn("{} is updating their dive pos too quickly!", player.getName().getString());
							correctDivePos(diver, responseSender);
							return;
						}
						Vec2i vec = new Vec2i(x, z);
						int dist = vec.squaredDistanceTo(diver.yttr$getDivePos());
						if (dist == 0) return;
						int moveSpeed = DIVING_BLOCKS_PER_TICK;
						ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
						if (!(is.getItem() instanceof SuitArmorItem)) return;
						SuitArmorItem sai = (SuitArmorItem)is.getItem();
						for (SuitResource sr : SuitResource.VALUES) {
							moveSpeed /= sr.getSpeedDivider(sai.getResourceAmount(is, sr) <= 0);
						}
						int max = (moveSpeed+1)*diff;
						if (dist > max*max) {
							LogManager.getLogger("Yttr").warn("{} dove too quickly! {}, {}", player.getName().getString(), x-diver.yttr$getDivePos().x, z-diver.yttr$getDivePos().z);
							correctDivePos(diver, responseSender);
							return;
						}
						int pressure = calculatePressure(player.getServerWorld(), diver.yttr$getDivePos().x, diver.yttr$getDivePos().z);
						for (SuitResource sr : SuitResource.VALUES) {
							sai.consumeResource(is, sr, sr.getConsumptionPerBlock(pressure)*(int)Math.sqrt(dist));
						}
						diver.yttr$setDivePos(vec);
						PacketByteBuf resp = new PacketByteBuf(Unpooled.buffer(8));
						resp.writeVarInt(pressure);
						responseSender.sendPacket(new Identifier("yttr", "dive_pressure"), resp);
					}
				});
			}
		});
		
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_to"), (server, player, handler, buf, responseSender) -> {
			if (player != null && player instanceof DiverPlayer) {
				DiverPlayer diver = (DiverPlayer)player;
				
				UUID id = buf.readUuid();
				server.execute(() -> {
					if (diver.yttr$isDiving() && diver.yttr$getFastDiveTarget() == null && diver.yttr$getKnownGeysers().contains(id)) {
						Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
						if (g != null) {
							double distance = Math.sqrt(g.pos.getSquaredDistance(diver.yttr$getDivePos().x, g.pos.getY(), diver.yttr$getDivePos().z, true));
							Multiset<SuitResource> resourcesNeeded = determineNeededResourcesForFastDive(distance);
							Multiset<SuitResource> resourcesAvailable = determineAvailableResources(player);
							for (SuitResource sr : SuitResource.VALUES) {
								if (resourcesAvailable.count(sr) < resourcesNeeded.count(sr)) {
									informCantDive(responseSender, "not enough "+sr.name().toLowerCase(Locale.ROOT));
									return;
								}
							}
							ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
							SuitArmorItem sai = (SuitArmorItem)is.getItem();
							for (SuitResource sr : SuitResource.VALUES) {
								sai.consumeResource(is, sr, resourcesNeeded.count(sr));
							}
							int time = (int)((distance/DIVING_BLOCKS_PER_TICK)/5);
							diver.yttr$setFastDiveTarget(g.pos);
							diver.yttr$setFastDiveTime(time);
							PacketByteBuf res = PacketByteBufs.create();
							for (SuitResource sr : SuitResource.VALUES) {
								res.writeVarInt(resourcesNeeded.count(sr));
							}
							res.writeVarInt(g.pos.getX());
							res.writeVarInt(g.pos.getZ());
							res.writeVarInt(time);
							responseSender.sendPacket(new Identifier("yttr", "animate_fastdive"), res);
						} else {
							informCantDive(responseSender, "unknown geyser");
							return;
						}
					} else {
						informCantDive(responseSender, "bad state");
						return;
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

	public static Multiset<SuitResource> determineAvailableResources(PlayerEntity player) {
		ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
		if (!(is.getItem() instanceof SuitArmorItem)) return ImmutableMultiset.of();
		SuitArmorItem sai = (SuitArmorItem)is.getItem();
		Multiset<SuitResource> resourcesAvailable = EnumMultiset.create(SuitResource.class);
		for (SuitResource sr : SuitResource.VALUES) {
			resourcesAvailable.add(sr, sai.getResourceAmount(is, sr));
		}
		return resourcesAvailable;
	}

	public static Multiset<SuitResource> determineNeededResourcesForFastDive(double distance) {
		int simulatedTicks = (int)(distance/DIVING_BLOCKS_PER_TICK);
		int distanceI = (int)distance;
		Multiset<SuitResource> resourcesNeeded = EnumMultiset.create(SuitResource.class);
		for (SuitResource sr : SuitResource.VALUES) {
			if (distance < 4 && sr == SuitResource.FUEL) continue;
			resourcesNeeded.add(sr, sr.getConsumptionPerTick(900)*simulatedTicks);
			resourcesNeeded.add(sr, sr.getConsumptionPerBlock(900)*distanceI);
		}
		return resourcesNeeded;
	}

	private void informCantDive(PacketSender responseSender, String msg) {
		PacketByteBuf res = PacketByteBufs.create();
		res.writeString(msg);
		responseSender.sendPacket(new Identifier("yttr", "cant_dive"), res);
	}

	private void correctDivePos(DiverPlayer diver, PacketSender responseSender) {
		PacketByteBuf resp = new PacketByteBuf(Unpooled.buffer(8));
		resp.writeInt(diver.yttr$getDivePos().x);
		resp.writeInt(diver.yttr$getDivePos().z);
		responseSender.sendPacket(new Identifier("yttr", "dive_pos"), resp);
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
		buf.writeInt((int)p.getX());
		buf.writeInt((int)p.getZ());
		GeysersState gs = GeysersState.get(p.getServerWorld());
		for (UUID u : ((DiverPlayer)p).yttr$getKnownGeysers()) {
			Geyser g = gs.getGeyser(u);
			if (g != null) {
				g.write(buf);
			}
		}
		ServerPlayNetworking.send(p, new Identifier("yttr", "dive"), buf);
	}

	public static void discoverGeyser(UUID id, ServerPlayerEntity player) {
		if (!(player instanceof DiverPlayer)) return;
		DiverPlayer diver = (DiverPlayer)player;
		Set<UUID> knownGeysers = diver.yttr$getKnownGeysers();
		if (!knownGeysers.contains(id)) {
			Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
			if (g == null) return;
			knownGeysers.add(id);
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			g.write(buf);
			ServerPlayNetworking.send(player, new Identifier("yttr", "discovered_geyser"), buf);
		}
	}
	
	public static int calculatePressure(ServerWorld world, int x, int z) {
		GeysersState gs = GeysersState.get(world);
		int absoluteMin = 100;
		int minPressure = 120;
		int maxPressure = 1000;
		int pressureGap = maxPressure-minPressure;
		int maxPressureGap = pressureGap+(minPressure-absoluteMin);
		int pressureEffect = 0;
		int falloff = 1536;
		int falloffSq = falloff*falloff;
		for (Geyser g : gs.getGeysersInRange(x, z, falloff)) {
			double distSq = g.pos.getSquaredDistance(x, g.pos.getY(), z, true);
			if (distSq < falloffSq) {
				double effect = (falloffSq-distSq)/falloffSq;
				pressureEffect += pressureGap*effect;
			}
		}
		return maxPressure-Math.min(maxPressureGap, pressureEffect);
	}

	public static List<ItemStack> asList(Inventory inv) {
		return asListExcluding(inv, -1);
	}
		
	public static List<ItemStack> asListExcluding(Inventory inv, int exclude) {
		return new AbstractList<ItemStack>() {
			
			@Override
			public ItemStack get(int index) {
				return index == exclude ? ItemStack.EMPTY : inv.getStack(index);
			}

			@Override
			public int size() {
				return inv.size();
			}
			
			@Override
			public ItemStack remove(int index) {
				if (index == exclude) return ItemStack.EMPTY;
				return inv.removeStack(index);
			}
			
			@Override
			public void clear() {
				inv.clear();
			}

			@Override
			public ItemStack set(int index, ItemStack element) {
				if (index == exclude) return ItemStack.EMPTY;
				ItemStack old = inv.getStack(index);
				inv.setStack(index, element);
				return old;
			}
			
		};
	}
	
}
