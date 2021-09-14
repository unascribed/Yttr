package com.unascribed.yttr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.TriConsumer;

import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YBrewing;
import com.unascribed.yttr.init.YCommands;
import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YFuels;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YNetworking;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.init.YScreenTypes;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.init.YTrades;
import com.unascribed.yttr.init.YWorldGen;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mechanics.TickAlwaysItemHandler;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.util.EquipmentSlots;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

public class Yttr implements ModInitializer {
	
	public static final int DIVING_BLOCKS_PER_TICK = 2;
	
	public static final Map<Identifier, SoundEvent> craftingSounds = Maps.newHashMap();
	
	public static boolean lessCreepyAwareHopper;
	
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
		YEnchantments.init();
		YStats.init();
		YBrewing.init();
		YTrades.init();
		YNetworking.init();
		YFuels.init();
		
		// TODO this should probably live somewhere else; this would be a nice time for an onPostInitialize {
		List<BlockState> states = DebugChunkGenerator.BLOCK_STATES;
		Set<BlockState> known = Sets.newHashSet(states);
		List<BlockState> newStates = Registry.BLOCK.stream()
			.flatMap(b -> b.getStateManager().getStates().stream())
			.filter(bs -> !known.contains(bs))
			.collect(Collectors.toList());
		if (newStates.isEmpty()) {
			YLog.info("Looks like someone else already fixed the debug world.", newStates.size());
		} else {
			YLog.info("Adding {} missing blockstates to the debug world.", newStates.size());
			states.addAll(newStates);
			int oldX = DebugChunkGenerator.X_SIDE_LENGTH;
			int oldZ = DebugChunkGenerator.Z_SIDE_LENGTH;
			DebugChunkGenerator.X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(states.size()));
			DebugChunkGenerator.Z_SIDE_LENGTH = MathHelper.ceil(states.size() / (float)DebugChunkGenerator.X_SIDE_LENGTH);
			YLog.info("Ok. Your debug world is now {}x{} instead of {}x{}.", DebugChunkGenerator.X_SIDE_LENGTH, DebugChunkGenerator.Z_SIDE_LENGTH, oldX, oldZ);
		}
		// }
		
		ServerTickEvents.START_WORLD_TICK.register(TickAlwaysItemHandler::startServerWorldTick);
		
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

	/**
	 * Invoke the given callback for every field of the given type in the given class. If an
	 * annotation type is supplied, the annotation on the field (if any) will be passed as the
	 * third argument to the callback.
	 * <p>
	 * This is the same method used by {@link #autoRegister}, so it can be used to scan fields in
	 * holder classes for additional information in later passes.
	 */
	public static <T, A extends Annotation> void eachRegisterableField(Class<?> holder, Class<T> type, Class<A> anno, TriConsumer<Field, T, A> cb) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					cb.accept(f, (T)f.get(null), anno == null ? null : f.getAnnotation(anno));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Scan a class {@code holder} for static final fields of type {@code type}, and register them
	 * in the yttr namespace with a path equal to the field's name as lower case in the given
	 * registry.
	 */
	public static <T> void autoRegister(Registry<T> registry, Class<?> holder, Class<? super T> type) {
		eachRegisterableField(holder, type, null, (f, v, na) -> {
			Registry.register(registry, "yttr:"+f.getName().toLowerCase(Locale.ROOT), (T)v);
		});
	}

	/**
	 * Serialize an Inventory to a ListTag. Unlike {@link Inventories#toTag}, this supports arbitrarily
	 * large stack sizes. Unlike {@link SimpleInventory#getTags}, this keeps slot indexes and therefore
	 * empty slots.
	 * @see #deserializeInv
	 */
	public static ListTag serializeInv(Inventory inv) {
		ListTag out = new ListTag();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (!is.isEmpty()) {
				CompoundTag c = is.toTag(new CompoundTag());
				if (is.getCount() > 127) {
					c.putInt("Count", is.getCount());
				}
				c.putInt("Slot", i);
				out.add(c);
			}
		}
		return out;
	}
	
	/**
	 * Deserialize a ListTag created by {@link #serializeInv} into the given Inventory. The
	 * Inventory will be cleared first. Can load large stacks written by serializeInv.
	 */
	public static void deserializeInv(ListTag tag, Inventory inv) {
		inv.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag c = tag.getCompound(i);
			int count = c.getInt("Count");
			if (count > 127) {
				c = c.copy();
				c.putInt("Count", 1);
			}
			ItemStack is = ItemStack.fromTag(c);
			is.setCount(count);
			inv.setStack(c.getInt("Slot"), is);
		}
	}

	/**
	 * @return {@code true} if the give entity is wearing a full Diving Suit
	 */
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
		int falloff = 768;
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

	/**
	 * Return a view of the given Inventory as a List. Modifications to the List will pass through
	 * to the Inventory.
	 */
	public static List<ItemStack> asList(Inventory inv) {
		return asListExcluding(inv, -1);
	}
	
	/**
	 * Return a view of the given Inventory as a List, excluding the given slot by treating it as
	 * empty. Modifications to the List will pass through to the Inventory, other than those to
	 * the excluded slot.
	 */
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
