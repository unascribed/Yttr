package com.unascribed.yttr.world;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

public class GeysersState extends PersistentState {

	private final Set<Geyser> geysers = Sets.newHashSet();
	private final Map<UUID, Geyser> geysersById = Maps.newHashMap();
	private final Map<BlockPos, Geyser> geysersByPos = Maps.newHashMap();
	
	public GeysersState() {
		super("yttr_geysers");
	}
	
	public static GeysersState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(GeysersState::new, "yttr_geysers");
	}
	
	public void addGeyser(Geyser g) {
		geysers.add(g);
		geysersById.put(g.id, g);
		geysersByPos.put(g.pos, g);
		markDirty();
	}
	
	public @Nullable Geyser getGeyser(UUID id) {
		return geysersById.get(id);
	}
	
	public @Nullable Geyser getGeyser(BlockPos pos) {
		return geysersByPos.get(pos);
	}
	
	public void removeGeyser(UUID id) {
		Geyser g = geysersById.remove(id);
		if (g != null) {
			geysers.remove(g);
			geysersByPos.remove(g.pos, g);
		}
	}
	
	public void removeGeyser(BlockPos pos) {
		Geyser g = geysersByPos.remove(pos);
		if (g != null) {
			geysers.remove(g);
			geysersById.remove(g.id, g);
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		geysers.clear();
		geysersById.clear();
		geysersByPos.clear();
		ListTag li = tag.getList("Geysers", NbtType.COMPOUND);
		for (int i = 0; i < li.size(); i++) {
			addGeyser(Geyser.fromTag(li.getCompound(i)));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag li = new ListTag();
		for (Geyser g : geysers) {
			li.add(g.toTag());
		}
		return tag;
	}
	
	

}
