package com.unascribed.yttr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.unascribed.yttr.inred.InRedLogic;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.YConfig.Trilean;
import com.unascribed.yttr.compat.EarsCompat;
import com.unascribed.yttr.compat.trinkets.YttrTrinketsCompat;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YBiomes;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YBrewing;
import com.unascribed.yttr.init.YCommands;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YEntities;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YFuels;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YLatches;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.init.YTrades;
import com.unascribed.yttr.init.YWorldGen;
import com.unascribed.yttr.init.conditional.YCopper;
import com.unascribed.yttr.init.conditional.YTrinkets;
import com.unascribed.yttr.mechanics.SoakingHandler;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mechanics.TickAlwaysItemHandler;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.network.MessageS2CDiscoveredGeyser;
import com.unascribed.yttr.network.MessageS2CDive;
import com.unascribed.yttr.util.EquipmentSlots;
import com.unascribed.yttr.util.SlotReference;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.util.annotate.RegisteredAs;
import com.unascribed.yttr.world.FilterNetworks;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;
import com.unascribed.yttr.world.WastelandPopulator;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

public class Yttr implements ModInitializer {
	
	public static Yttr INST;
	
	public Yttr() {
		if (INST != null) throw new AssertionError("Double initialized!");
		INST = this;
	}
	
	public static boolean COPPER_FALLBACK_ACTIVE = false;
	
	public static final int DIVING_BLOCKS_PER_TICK = 2;
	
	public static final Map<Identifier, SoundEvent> craftingSounds = Maps.newHashMap();
	
	public static boolean lessCreepyAwareHopper;
	
	public static final List<DelayedTask> delayedServerTasks = Lists.newArrayList();
	
	public static Function<PlayerEntity, ItemStack> getSoleTrinket = pe -> ItemStack.EMPTY;
	public static Function<PlayerEntity, ItemStack> getBackTrinket = pe -> ItemStack.EMPTY;
	public static Predicate<PlayerEntity> isVisuallyWearingBoots = pe -> !pe.getEquippedStack(EquipmentSlot.FEET).isEmpty();
	
	@Override
	public void onInitialize() {
		// base content
		YBlocks.init();
		YBlockEntities.init();
		YItems.init();
		YSounds.init();
		YFluids.init();
		YEntities.init();
		YWorldGen.init();
		YBiomes.init();
		
		// auxillary content
		YStatusEffects.init();
		YRecipeTypes.init();
		YRecipeSerializers.init();
		YCommands.init();
		YTags.init();
		YHandledScreens.init();
		YEnchantments.init();
		
		// general initialization
		YStats.init();
		YCriteria.init();
		YBrewing.init();
		YTrades.init();
		YNetwork.init();
		YFuels.init();
		YItemGroups.init();
		
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (player instanceof ServerPlayerEntity) {
				YCriteria.BROKE_BLOCK.trigger((ServerPlayerEntity)player, pos, state, player.getStackInHand(Hand.MAIN_HAND));
			}
		});
		
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			TickAlwaysItemHandler.startServerWorldTick(world);
			SoakingHandler.startServerWorldTick(world);
			FilterNetworks.get(world).tick();
		});
		ServerTickEvents.END_WORLD_TICK.register(SoakingHandler::endServerWorldTick);
		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			InRedLogic.onServerTick();
			Iterator<DelayedTask> iter = delayedServerTasks.iterator();
			int tasksRunThisTick = 0;
			while (iter.hasNext()) {
				DelayedTask dt = iter.next();
				if (tasksRunThisTick > 10 && !dt.important) continue;
				if (dt.delay-- <= 0) {
					dt.r.run();
					tasksRunThisTick++;
					iter.remove();
				}
			}
		});
		
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, mgr) -> {
			Substitutes.reload(mgr.getResourceManager());
		});
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			if (WastelandPopulator.isEligible(world, chunk)) {
				world.getServer().send(new ServerTask(world.getServer().getTicks(), () -> {
					WastelandPopulator.populate(world.getSeed(), world, chunk.getPos());
				}));
			}
		});
		
		if (FabricLoader.getInstance().isModLoaded("ears")) {
			try {
				EarsCompat.init();
			} catch (Throwable t) {}
		}
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			try {
				YttrTrinketsCompat.init();
				YTrinkets.init();
			} catch (Throwable t) {}
		}
	}
	
	public void onPostInitialize() {
		if (YConfig.General.fixupDebugWorld) {
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
		}
		
		if (YConfig.WorldGen.copper == Trilean.AUTO) {
			boolean foundCopper = false;
			for (Map.Entry<RegistryKey<Item>, Item> id : Registry.ITEM.getEntries()) {
				if (id.getKey().getValue().getPath().contains("copper_ingot")) {
					YLog.info("Found a copper ingot supplied by {}", id.getKey().getValue().getNamespace());
					YLog.info("Note that this check does not guarantee this copper ingot will be recognized by Yttr; if it isn't, make sure it's in the c:copper_ingots tag.");
					foundCopper = true;
					break;
				}
			}
			if (!foundCopper) {
				YLog.warn("I can't find a copper ingot, so I'm adding my own!");
				YCopper.init();
			}
		} else if (YConfig.WorldGen.copper == Trilean.ON) {
			YCopper.init();
		}
		
		YLatches.latchAll();
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
	@SuppressWarnings("unchecked")
	public static <T, A extends Annotation> void eachRegisterableField(Class<?> holder, Class<T> type, Class<A> anno, TriConsumer<Field, T, A> cb) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					f.setAccessible(true);
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
	@SuppressWarnings("unchecked")
	public static <T> void autoRegister(Registry<T> registry, Class<?> holder, Class<? super T> type) {
		eachRegisterableField(holder, type, RegisteredAs.class, (f, v, ann) -> {
			String id;
			if (ann != null) {
				if (ann.value().contains(":")) {
					id = ann.value();
				} else {
					id = "yttr:"+ann.value();
				}
			} else {
				id = "yttr:"+f.getName().toLowerCase(Locale.ROOT);
			}
			Registry.register(registry, id, (T)v);
		});
	}

	/**
	 * Scan a class {@code holder} for static final fields of type {@code type}, and register them
	 * in the yttr namespace with a path equal to the field's name as lower case in the given
	 * ad-hoc registry.
	 */
	public static <T> void autoRegister(Consumer<T> adhocRegistry, Class<?> holder, Class<T> type) {
		eachRegisterableField(holder, type, null, (f, v, na) -> {
			adhocRegistry.accept(v);
		});
	}

	/**
	 * Serialize an Inventory to a ListTag. Unlike {@link Inventories#writeNbt}, this supports arbitrarily
	 * large stack sizes. Unlike {@link SimpleInventory#toNbtList}, this keeps slot indexes and therefore
	 * empty slots.
	 * @see #deserializeInv
	 */
	public static NbtList serializeInv(Inventory inv) {
		NbtList out = new NbtList();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (!is.isEmpty()) {
				NbtCompound c = is.writeNbt(new NbtCompound());
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
	public static void deserializeInv(NbtList tag, Inventory inv) {
		inv.clear();
		for (int i = 0; i < tag.size(); i++) {
			NbtCompound c = tag.getCompound(i);
			int count = c.getInt("Count");
			if (count > 127) {
				c = c.copy();
				c.putInt("Count", 1);
			}
			ItemStack is = ItemStack.fromNbt(c);
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
		GeysersState gs = GeysersState.get(p.getServerWorld());
		List<Geyser> geysers = ((DiverPlayer)p).yttr$getKnownGeysers().stream()
				.map(gs::getGeyser).filter(g -> g != null)
				.collect(Collectors.toList());
		new MessageS2CDive((int)p.getPos().x, (int)p.getPos().z, geysers).sendTo(p);
	}

	public static void discoverGeyser(UUID id, ServerPlayerEntity player) {
		if (!(player instanceof DiverPlayer)) return;
		DiverPlayer diver = (DiverPlayer)player;
		Set<UUID> knownGeysers = diver.yttr$getKnownGeysers();
		if (!knownGeysers.contains(id)) {
			Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
			if (g == null) return;
			knownGeysers.add(id);
			new MessageS2CDiscoveredGeyser(g).sendTo(player);
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

	public static int getSpringingLevel(PlayerEntity p) {
		ItemStack is = getSoleTrinket.apply(p);
		if (YEnchantments.SPRINGING.isPresent()) {
			return EnchantmentHelper.getLevel(YEnchantments.SPRINGING.get(), is);
		}
		return 0;
	}

	public static boolean isWearingCoil(PlayerEntity e) {
		return YItems.CUPROSTEEL_COIL.is(getSoleTrinket.apply(e).getItem());
	}

	public static @Nullable SlotReference scanInventory(Inventory inv, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (predicate.test(is)) {
				return new SlotReference(inv, i);
			}
		}
		return null;
	}
	
}
