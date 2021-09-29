package com.unascribed.yttr.world;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.world.FilterNetwork.Node;
import com.unascribed.yttr.world.FilterNetwork.NodeType;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PersistentState;

public class FilterNetworks extends PersistentState {

	protected ServerWorld world;
	private final Map<UUID, FilterNetwork> networks = Maps.newHashMap();
	protected final Map<BlockPos, FilterNetwork> networksByPos = Maps.newHashMap();
	
	public FilterNetworks() {
		super("yttr_filter_networks");
	}
	
	public static FilterNetworks get(ServerWorld world) {
		FilterNetworks fn = world.getPersistentStateManager().getOrCreate(FilterNetworks::new, "yttr_filter_networks");
		fn.world = world;
		return fn;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		networks.clear();
		networksByPos.clear();
		NbtCompound networks = tag.getCompound("Networks");
		for (String k : networks.getKeys()) {
			UUID id = UUID.fromString(k);
			FilterNetwork fn = new FilterNetwork(this, id);
			fn.readNbt(networks.getCompound(k));
			addNetworkDirectly(fn);
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		NbtCompound networks = new NbtCompound();
		for (FilterNetwork fn : this.networks.values()) {
			NbtCompound en = new NbtCompound();
			fn.writeNbt(en);
			networks.put(fn.getId().toString(), en);
		}
		nbt.put("Networks", networks);
		return nbt;
	}
	
	public void tick() {
		for (FilterNetwork fn : networks.values()) {
			fn.tick();
		}
	}
	
	public Optional<FilterNetwork> getNetworkAt(BlockPos pos) {
		return Optional.ofNullable(networksByPos.get(pos));
	}

	public void addNetwork(FilterNetwork network) {
		addNetworkDirectly(network);
		markDirty();
	}
	
	private void addNetworkDirectly(FilterNetwork network) {
		if (network.getOwner() != this) throw new IllegalArgumentException("Network does not belong to this world");
		networks.put(network.getId(), network);
		network.onAdded();
	}

	public void removeNetwork(FilterNetwork network) {
		removeNetworkDirectly(network);
		markDirty();
	}

	private void removeNetworkDirectly(FilterNetwork network) {
		if (network.getOwner() != this) throw new IllegalArgumentException("Network does not belong to this world");
		networks.remove(network.getId(), network);
		network.onRemoved();
	}

	public void introduce(BlockPos pos, NodeType type) {
		Node n = new Node(pos, type);
		if (networksByPos.containsKey(pos)) {
			networksByPos.get(pos).addNode(n);
			return;
		}
		for (BlockPos neighbor : neighbors(pos)) {
			if (networksByPos.containsKey(neighbor)) {
				networksByPos.get(neighbor).addNode(n);
				return;
			}
		}
		FilterNetwork net = new FilterNetwork(this, UUID.randomUUID());
		YLog.debug("Creating new network {} for orphan at {}", net.getId(), pos.toShortString());
		net.addNode(n);
		addNetwork(net);
	}

	public void destroy(BlockPos pos) {
		if (networksByPos.containsKey(pos)) {
			networksByPos.get(pos).removeNodeAt(pos);
		}
	}

	private static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());
	
	public static Set<BlockPos> neighbors(BlockPos pos) {
		BlockPos fpos = pos.toImmutable();
		return new AbstractSet<BlockPos>() {
			@Override
			public Iterator<BlockPos> iterator() {
				return new AbstractIterator<BlockPos>() {
					private final BlockPos.Mutable mut = new BlockPos.Mutable();
					private final Iterator<Direction> dirIter = DIRECTIONS.iterator();
					
					@Override
					protected BlockPos computeNext() {
						if (!dirIter.hasNext()) return endOfData();
						return mut.set(fpos).move(dirIter.next());
					}
				};
			}
			
			@Override
			public boolean contains(Object o) {
				return o instanceof BlockPos && fpos.getManhattanDistance((BlockPos)o) == 1;
			}
			
			@Override
			public int size() {
				return DIRECTIONS.size();
			}
		};
	}
	
}
