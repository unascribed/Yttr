package com.unascribed.yttr.world;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.util.math.Vec2i;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

public class GeysersState extends PersistentState {

	private final Set<Geyser> geysers = Sets.newHashSet();
	private final Map<UUID, Geyser> geysersById = Maps.newHashMap();
	private final Map<BlockPos, Geyser> geysersByPos = Maps.newHashMap();
	private final Multimap<ChunkPos, Geyser> geysersByChunk = HashMultimap.create();
	private final Multimap<Vec2i, Geyser> geysersByRegion = HashMultimap.create();
	
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
		geysersByChunk.put(g.chunkPos, g);
		geysersByRegion.put(g.regionPos, g);
		markDirty();
	}
	
	public @Nullable Geyser getGeyser(UUID id) {
		return geysersById.get(id);
	}
	
	public @Nullable Geyser getGeyser(BlockPos pos) {
		return geysersByPos.get(pos);
	}
	
	public Collection<Geyser> getGeysersInChunk(ChunkPos pos) {
		return geysersByChunk.get(pos);
	}
	
	public Collection<Geyser> getGeysersInRegion(int x, int z) {
		return geysersByRegion.get(new Vec2i(x, z));
	}
	
	public Collection<Geyser> getGeysersInRange(int x, int z, int range) {
		if (range <= 0) return Collections.emptyList();
		int rangeSq = range*range;
		int chunkRadius = ((range+15)/16)+1;
		if (chunkRadius > 16) {
			Collection<Geyser> out = Collections.emptyList();
			int regionRadius = ((range+511)/512)+1;
			int rX = x/512;
			int rZ = z/512;
			for (int rXo = -regionRadius; rXo <= regionRadius; rXo++) {
				for (int rZo = -regionRadius; rZo <= regionRadius; rZo++) {
					for (Geyser g : getGeysersInRegion(rX+rXo, rZ+rZo)) {
						if (g.pos.getSquaredDistance(x, g.pos.getY(), z, true) < rangeSq) {
							if (out.isEmpty()) out = Lists.newArrayList();
							out.add(g);
						}
					}
				}
			}
			return out;
		} else {
			Collection<Geyser> out = Collections.emptyList();
			int cX = x/16;
			int cZ = z/16;
			for (int cXo = -chunkRadius; cXo <= chunkRadius; cXo++) {
				for (int cZo = -chunkRadius; cZo <= chunkRadius; cZo++) {
					for (Geyser g : getGeysersInChunk(new ChunkPos(cX+cXo, cZ+cZo))) {
						if (g.pos.getSquaredDistance(x, g.pos.getY(), z, true) < rangeSq) {
							if (out.isEmpty()) out = Lists.newArrayList();
							out.add(g);
						}
					}
				}
			}
			return out;
		}
	}
	
	public void removeGeyser(UUID id) {
		Geyser g = geysersById.remove(id);
		if (g != null) {
			geysers.remove(g);
			geysersByPos.remove(g.pos, g);
			geysersByChunk.remove(g.chunkPos, g);
			geysersByRegion.remove(g.regionPos, g);
		}
	}
	
	public void removeGeyser(BlockPos pos) {
		Geyser g = geysersByPos.remove(pos);
		if (g != null) {
			geysers.remove(g);
			geysersById.remove(g.id, g);
			geysersByChunk.remove(g.chunkPos, g);
			geysersByRegion.remove(g.regionPos, g);
		}
	}

	@Override
	public void readNbt(NbtCompound tag) {
		geysers.clear();
		geysersById.clear();
		geysersByPos.clear();
		geysersByChunk.clear();
		geysersByRegion.clear();
		NbtList li = tag.getList("Geysers", NbtType.COMPOUND);
		for (int i = 0; i < li.size(); i++) {
			addGeyser(Geyser.fromTag(li.getCompound(i)));
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		NbtList li = new NbtList();
		for (Geyser g : geysers) {
			li.add(g.toTag());
		}
		tag.put("Geysers", li);
		return tag;
	}

}
