package com.unascribed.yttr.inred;

import com.unascribed.yttr.init.YBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

public class InRedLogic {
	public static final int MAX_SIGNAL = 0b11_1111; //TODO: stay with 0-63?
	private static final int INTER_IR_TICKS = 1;
	public static int tickCount = 0;

	public static Consumer<MinecraftServer> onServerTick = server -> { //TODO: hook to server tick event
		tickCount++;
		if (tickCount > INTER_IR_TICKS)
			tickCount = 0;
	};

	public static boolean isIRTick() {
		return (tickCount == 0);
	}

	/**
	 * Searches for the highest IR signal which can be delivered to the indicated
	 * block face.
	 *
	 * @param world
	 *            The world the device resides in
	 * @param devicePos
	 *            The location of the device
	 * @param dir
	 *            The direction *from* the device *towards* where the prospective
	 *            signal is coming from.
	 * @return The IR value, or the redstone level if no IR is present, or 0 if
	 *         nothing is present.
	 */
	public static int findIRValue(World world, BlockPos devicePos, Direction dir) {
		BlockPos initialPos = devicePos.offset(dir);

		if (!checkCandidacy(world, initialPos, dir.getOpposite())) {
			BlockPos up = initialPos.up();
			if (checkCandidacy(world, up, dir.getOpposite()) && world.getBlockState(up).getBlock() != YBlocks.INRED_SCAFFOLD) {
				initialPos = up;
			} else {
				BlockPos down = initialPos.down();
				if (checkCandidacy(world, down, dir.getOpposite()) && world.getBlockState(down).getBlock() != YBlocks.INRED_SCAFFOLD) {
					initialPos = down;
				} else {
					return (world.getEmittedRedstonePower(initialPos, dir) != 0) ? 1 : 0;
				}
			}
		}

		if (world.isAir(initialPos)) return 0;
		BlockState initialState = world.getBlockState(initialPos);
		if (initialState.getBlock() == YBlocks.INRED_CABLE || initialState.getBlock() == YBlocks.INRED_SCAFFOLD) {
			// Search!
			return wireSearch(world, devicePos, dir);
		}

		if (initialState.getBlock() instanceof InRedProvider) {
			// We have an InRed provider behind us. Excellent! Check if it has a
			// component before we keep searching.
			InRedDevice device = ((InRedProvider) initialState.getBlock()).getDevice(world, initialPos, initialState, dir.getOpposite());
			if (device != null) {
				// The provider has a component for this side. Fantastic! Don't
				// search, just get its value.
				return device.getSignalValue();
			}
		}

		// Oh. Okay. No wires or machines. Well, return the vanilla redstone value as
		// the bottom bit here and call it a day.
		return (world.getEmittedRedstonePower(initialPos, dir) != 0) ? 1 : 0;
	}

	public static boolean checkCandidacy(World world, BlockPos pos, Direction side) {
		if (world.isAir(pos)) return false;

		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == YBlocks.INRED_CABLE || state.getBlock() == YBlocks.INRED_SCAFFOLD) return true;
		if (state.getBlock() instanceof InRedProvider) {
			return ((InRedProvider) state.getBlock()).getDevice(world, pos, state, side) != null;
		}
		return false;
	}

	private static final Direction[] PLANAR_FACINGS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

	public static boolean canConnect(BlockView world, BlockPos pos, Direction from) {
		if (world.getBlockState(pos).isAir()) return false;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == YBlocks.INRED_CABLE || block == YBlocks.INRED_SCAFFOLD) return true;
		if (block instanceof InRedProvider) {
			return ((InRedProvider)block).getDevice(world, pos, state, from) != null;
		}

		return false;
	}

	public static boolean isSideSolid(World world, BlockPos pos, Direction dir) {
		return Block.isFaceFullSquare(world.getBlockState(pos).getOutlineShape(world, pos), dir);
	}

	private static int wireSearch(World world, BlockPos device, Direction dir) {
		int depth = 0;
		Set<Endpoint> rejected = new HashSet<>();
		Set<BlockPos> traversed = new HashSet<>();
		List<Endpoint> members = new ArrayList<>();
		List<Endpoint> queue = new ArrayList<>();
		List<Endpoint> next = new ArrayList<>();

		queue.add(new Endpoint(device.offset(dir), dir.getOpposite()));

		if (device.getY() < 255 && !isSideSolid(world, device.offset(Direction.UP), Direction.DOWN)) queue.add(new Endpoint(device.offset(dir).up(), dir.getOpposite()));
		if (device.getY() > 0 && !isSideSolid(world, device.offset(dir), dir.getOpposite())) queue.add(new Endpoint(device.offset(dir).down(), dir.getOpposite()));

		while (!queue.isEmpty() || !next.isEmpty()) {
			if (queue.isEmpty()) {
				depth++;
				if (depth > 63) return 0; // We've searched too far, there's no signal in range.
				queue.addAll(next);
				next.clear();
			}

			Endpoint cur = queue.remove(0);

			if (world.isAir(cur.pos)) continue;
			BlockState state = world.getBlockState(cur.pos);

			Block block = state.getBlock();
			if (block == YBlocks.INRED_CABLE || block == YBlocks.INRED_SCAFFOLD) { //TODO: framework for cables?
				traversed.add(cur.pos);

				if (block == YBlocks.INRED_CABLE) {
					// Add neighbors
					for (Direction facing : PLANAR_FACINGS) {
						BlockPos offset = cur.pos.offset(facing);

						if (offset.getY() < 255 && !isSideSolid(world, cur.pos.up(), Direction.DOWN)) checkAdd(new Endpoint(offset.up(), facing.getOpposite()), next, traversed, rejected);
						if (offset.getY() > 0 && !isSideSolid(world, offset, facing.getOpposite()) && specialCaseWire(world, offset.down())) checkAdd(new Endpoint(offset.down(), facing.getOpposite()), next, traversed, rejected);
						if (facing == cur.facing) continue; // Don't try to bounce back to the block we came from
						checkAdd(new Endpoint(offset, facing.getOpposite()), next, traversed, rejected);
					}
				} else if (block == YBlocks.INRED_SCAFFOLD) {
					for(Direction facing : Direction.values()) {
						BlockPos offset = cur.pos.offset(facing);
						if (offset.getY()<0 || offset.getY()>255) continue;
						checkAdd(new Endpoint(offset, facing.getOpposite()), next, traversed, rejected);
						if (facing != Direction.UP && facing != Direction.DOWN) {
							BlockPos specialCase = offset.down();
							if (specialCase.getY() < 0) continue;
							if (world.getBlockState(specialCase).getBlock() == YBlocks.INRED_CABLE) checkAdd(new Endpoint(specialCase, facing.getOpposite()), next, traversed, rejected);
						}
					}
				}

				continue;
			}

			Integer rightHere = valueDirectlyAt(world, cur.pos, cur.facing);
			if (rightHere != null) {
				members.add(cur);
				rejected.add(cur);
				continue;
			}
		}

		// Grab the bitwise OR of all signals
		int result = 0;
		for (Endpoint cur : members) {
			int val = valueDirectlyAt(world, cur.pos, cur.facing);
			result |= val;
		}
		return result;
	}

	private static boolean specialCaseWire(World world, BlockPos target) {
		return world.getBlockState(target).getBlock() != YBlocks.INRED_BLOCK && world.getBlockState(target).getBlock() != YBlocks.INRED_SCAFFOLD;
	}

	private static void checkAdd(Endpoint endpoint, List<Endpoint> next, Set<BlockPos> traversed, Set<Endpoint> rejected) {
		if (traversed.contains(endpoint.pos)) return;
		if (rejected.contains(endpoint)) return;
		next.add(endpoint);
	}

	public static Integer valueDirectlyAt(World world, BlockPos pos, Direction dir) {
		if (world.isAir(pos)) return null;
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == YBlocks.INRED_CABLE || block == YBlocks.INRED_SCAFFOLD) return null; // wires don't carry power directly
		if (block instanceof InRedProvider) {
			InRedDevice comp = ((InRedProvider) block).getDevice(world, pos, state, dir);
			if (comp != null) {
				return comp.getSignalValue();
			}
		}
//        if (world.getEmittedRedstonePower(pos, dir) != 0) return 1; TODO: maybe have this? maybe not
		return null;
	}

	private static class Endpoint {
		BlockPos pos;
		Direction facing;

		public Endpoint(BlockPos pos, Direction facing) {
			this.pos = pos;
			this.facing = facing;
		}

		@Override
		public int hashCode() {
			return Objects.hash(pos, facing);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
			if (!(other instanceof Endpoint)) return false;
			Endpoint otherEnd = (Endpoint) other;
			return Objects.equals(pos, otherEnd.pos) && Objects.equals(facing, otherEnd.facing);
		}

		@Override
		public String toString() {
			return "{x:" + pos.getX() + ", y:" + pos.getY() + ", z:" + pos.getZ() + ", dir:" + facing + "}";
		}

	}
}
