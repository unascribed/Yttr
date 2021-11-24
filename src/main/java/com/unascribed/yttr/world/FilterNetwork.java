package com.unascribed.yttr.world;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.unascribed.yttr.content.block.device.VoidFilterBlock;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.util.NBTUtils;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Ascii;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

public class FilterNetwork {

	public enum NodeType {
		PIPE,
		FILTER,
		DEAD_FILTER,
		DSU,
		TANK,
	}
	
	public static class Node {
		private final BlockPos pos;
		private final NodeType type;
		public Node(BlockPos pos, NodeType type) {
			this.pos = pos.toImmutable();
			this.type = type;
		}
		public BlockPos getPos() {
			return pos;
		}
		public NodeType getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return type+"@"+pos.toShortString().replace(" ", "");
		}
	}
	
	private final FilterNetworks owner;
	private UUID id;
	private final MutableGraph<Node> members = GraphBuilder.undirected()
			.allowsSelfLoops(true)
			.nodeOrder(ElementOrder.unordered())
			.build();
	
	private final Map<BlockPos, Node> membersByPos = Maps.newHashMap();
	private final Multimap<NodeType, Node> membersByType = HashMultimap.create();
	
	private int totalFluidCapacity;
	private int fluidProductionPerTick;
	
	private int fluidContent;
	
	
	
	public FilterNetwork(FilterNetworks owner) {
		this.owner = owner;
	}
	
	public FilterNetwork(FilterNetworks owner, UUID id) {
		this(owner);
		this.id = id;
	}

	private void removeNodeDirectly(Node n) {
		members.removeNode(n);
		membersByPos.remove(n.pos, n);
		membersByType.remove(n.type, n);
		owner.networksByPos.remove(n.pos, this);
	}
	
	private void addNodeDirectly(Node n) {
		if (membersByPos.containsKey(n.pos)) {
			removeNodeDirectly(membersByPos.get(n.pos));
		}
		members.addNode(n);
		membersByPos.put(n.pos, n);
		membersByType.put(n.type, n);
		owner.networksByPos.put(n.pos, this);
	}
	
	public void removeNodeAt(BlockPos pos) {
		YLog.trace("Removing {} from network {}", pos, id);
		if (membersByPos.containsKey(pos)) {
			Node node = membersByPos.get(pos);
			Set<Node> neighbors = Sets.newHashSet(members.adjacentNodes(node));
			removeNodeDirectly(node);
			if (neighbors.size() > 1) {
				for (Node a : neighbors) {
					if (!members.nodes().contains(a)) continue;
					for (Node b : neighbors) {
						if (!members.nodes().contains(b)) continue;
						if (a == b) continue;
						if (!isReachable(a, b)) {
							FilterNetwork other = new FilterNetwork(owner, UUID.randomUUID());
							YLog.debug("Splitting network {} of size {} off into {}", id, size(), other.id);
							Set<Node> reachable = Graphs.reachableNodes(members, b);
							for (int p = 0; p < 3; p++) {
								for (Node n : reachable) {
									if (p == 0) {
										other.addNodeDirectly(n);
									} else if (p == 1) {
										for (Node n2 : members.adjacentNodes(n)) {
											other.members.putEdge(n, n2);
										}
									} else if (p == 2) {
										removeNodeDirectly(n);
									}
								}
							}
							other.update();
							YLog.debug("Network {} is now of size {}, and {} is of size {}", id, size(), other.id, other.size());
							owner.addNetwork(other);
						}
					}
				}
			}
			update();
			owner.markDirty();
			if (isEmpty()) {
				YLog.debug("Destroying empty network {}", id);
				owner.removeNetwork(this);
			}
		}
	}
	
	public void addNode(Node node) {
		Node cur = membersByPos.get(node.pos);
		if (cur != null && cur.type == node.type) return;
		Set<BlockPos> neighbors = FilterNetworks.neighbors(node.pos);
		if (cur != null && !isEmpty() && neighbors.stream().noneMatch(membersByPos::containsKey)) {
			throw new IllegalArgumentException("Cannot add orphan node at "+node.pos+" to non-empty network");
		}
		addNodeDirectly(node);
		YLog.debug("Adding {} to network {}", node.pos.toShortString(), id);
		for (BlockPos neighbor : neighbors) {
			Node n = membersByPos.get(neighbor);
			if (n != null) {
				members.putEdge(node, n);
			} else {
				FilterNetwork other = owner.networksByPos.get(neighbor);
				if (other != null) {
					Node originator = other.membersByPos.get(neighbor);
					if (originator != null) {
						YLog.debug("Joining network {} of size {} and network {} of size {}", id, size(), other.id, other.size());
						for (int p = 0; p < 2; p++) {
							for (Node no : other.members.nodes()) {
								if (p == 0) {
									addNodeDirectly(no);
								} else if (p == 1) {
									for (Node no2 : other.members.adjacentNodes(no)) {
										members.putEdge(no, no2);
									}
								}
							}
						}
						members.putEdge(node, originator);
						YLog.debug("Network {} is now of size {}. Destroying {}", id, size(), other.id);
						owner.removeNetwork(other);
					}
				}
			}
		}
		update();
		owner.markDirty();
	}
	
	public void tick() {
		
	}
	
	private void update() {
		boolean complete = isComplete();
		for (Node n : membersByType.get(NodeType.FILTER)) {
			BlockState bs = owner.world.getBlockState(n.pos);
			if (bs.isOf(YBlocks.VOID_FILTER)) {
				owner.world.setBlockState(n.pos, bs.with(VoidFilterBlock.INDEPENDENT, !complete));
			}
		}
		totalFluidCapacity = (membersByType.get(NodeType.PIPE).size()*100) + (membersByType.get(NodeType.TANK).size()*64000);
		fluidProductionPerTick = membersByType.get(NodeType.FILTER).size()*2;
		System.out.println("total capacity: "+totalFluidCapacity);
		System.out.println("production per tick: "+fluidProductionPerTick);
		if (fluidProductionPerTick > 0) {
			System.out.println("time to destruction: "+((totalFluidCapacity/fluidProductionPerTick)/1200)+"m");
		} else {
			System.out.println("time to destruction: ∞");
		}
	}
	
	public void clear() {
		for (BlockPos bp : membersByPos.keySet()) {
			owner.networksByPos.remove(bp, this);
		}
		members.nodes().clear();
		membersByPos.clear();
		membersByType.clear();
		totalFluidCapacity = 0;
	}

	public FilterNetworks getOwner() {
		return owner;
	}
	
	public UUID getId() {
		return id;
	}
	
	private boolean isReachable(Node from, Node to) {
		return Graphs.reachableNodes(members, from).contains(to);
	}

	public int size() {
		return members.nodes().size();
	}
	
	public boolean isEmpty() {
		return members.nodes().isEmpty();
	}
	
	public boolean isComplete() {
		return membersByType.containsKey(NodeType.TANK)
				&& membersByType.containsKey(NodeType.DSU)
				&& membersByType.containsKey(NodeType.FILTER);
	}

	public void onAdded() {
		for (BlockPos bp : membersByPos.keySet()) {
			owner.networksByPos.put(bp, this);
		}
	}

	public void onRemoved() {
		for (BlockPos bp : membersByPos.keySet()) {
			owner.networksByPos.remove(bp, this);
		}
	}
	
	public void readNbt(NbtCompound compound) {
		NbtList li = compound.getList("Nodes", NbtType.COMPOUND);
		List<Node> nodes = Lists.newArrayList();
		List<Map.Entry<Node, int[]>> conns = Lists.newArrayList();
		clear();
		for (int i = 0; i < li.size(); i++) {
			NbtCompound en = li.getCompound(i);
			BlockPos pos = NBTUtils.listToBlockPos(en.getList("Pos", NbtType.INT));
			NodeType type = NodeType.valueOf(Ascii.toUpperCase(en.getString("Type")));
			Node n = new Node(pos, type);
			nodes.add(n);
			conns.add(Maps.immutableEntry(n, en.getIntArray("Conn")));
			addNodeDirectly(n);
		}
		for (Map.Entry<Node, int[]> en : conns) {
			Node n = en.getKey();
			int[] conn = en.getValue();
			for (int c : conn) {
				Node cn = nodes.get(c);
				members.putEdge(n, cn);
			}
		}
		compound.putInt("FluidContent", fluidContent);
	}
	
	public void writeNbt(NbtCompound compound) {
		List<Node> nodes = Lists.newArrayList(members.nodes());
		NbtList li = new NbtList();
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			NbtCompound en = new NbtCompound();
			en.put("Pos", NBTUtils.blockPosToList(n.pos));
			en.putString("Type", Ascii.toLowerCase(n.type.name()));
			IntArrayList conn = new IntArrayList();
			for (Node ne : members.adjacentNodes(n)) {
				conn.add(nodes.indexOf(ne));
			}
			en.put("Conn", new NbtIntArray(conn.toIntArray()));
			li.add(en);
		}
		compound.put("Nodes", li);
		fluidContent = compound.getInt("FluidContent");
		update();
	}

	public int getPressure() {
		return 0;
	}
	
}
