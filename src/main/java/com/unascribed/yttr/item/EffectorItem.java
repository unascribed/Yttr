package com.unascribed.yttr.item;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.EffectorWorld;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

public class EffectorItem extends Item {

	public EffectorItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!(world instanceof ServerWorld)) return ActionResult.SUCCESS;
		BlockPos pos = context.getBlockPos();
		Direction dir = context.getSide().getOpposite();
		ItemStack stack = context.getStack();
		setFuel(stack, 512);
		int fuel = getFuel(stack);
		int amt = effect(world, pos, dir, stack, Math.min(fuel, 32), true);
		setFuel(stack, fuel-amt);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(pos);
		buf.writeByte(dir.ordinal());
		buf.writeByte(amt);
		((ServerWorld)world).getChunkManager().sendToNearbyPlayers(context.getPlayer(), ServerPlayNetworking.createS2CPacket(new Identifier("yttr", "effector"), buf));
		return ActionResult.SUCCESS;
	}
	
	public int getFuel(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt("Fuel") : 0;
	}
	
	public void setFuel(ItemStack stack, int fuel) {
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		stack.getTag().putInt("Fuel", fuel);
	}

	public interface RenderUpdateCallback {
		void scheduleRenderUpdate(int x, int y, int z);
	}
	
	public static int effect(World world, BlockPos pos, Direction dir, @Nullable ItemStack stack, int distance, boolean server) {
		if (!(world instanceof EffectorWorld)) return 0;
		EffectorWorld ew = (EffectorWorld)world;
		BlockPos.Mutable cursor = pos.mutableCopy();
		BlockPos.Mutable outerCursor = new BlockPos.Mutable();
		Axis mainAxis = dir.getAxis();
		List<Axis> axes = Arrays.asList(Direction.Axis.values());
		Axis otherAxis1 = Iterables.find(axes, a -> a != mainAxis);
		Axis otherAxis2 = Iterables.find(Lists.reverse(axes), a -> a != mainAxis);
		int hits = 0;
		for (int i = 0; i < distance; i++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					outerCursor.set(cursor);
					move(outerCursor, otherAxis1, x);
					move(outerCursor, otherAxis2, z);
					BlockState bs = world.getBlockState(outerCursor);
					if (bs.getHardness(world, outerCursor) < 0) continue;
					if (server) {
						// we'd like to have accurate collision for the animation
						ew.yttr$addPhaseBlock(outerCursor, 100-(i/2), (i/2));
					} else {
						// on the client, we can do the animation without rebaking chunks, so let's not
						ew.yttr$addPhaseBlock(outerCursor, 100, 0);
					}
				}
			}
			hits++;
			cursor.move(dir);
			if (server && world.isAir(cursor)) {
				break;
			}
		}
		return hits;
	}

	private static void move(BlockPos.Mutable mut, Axis axis, int distance) {
		if (distance != 0) {
			int x = axis == Axis.X ? distance : 0;
			int y = axis == Axis.Y ? distance : 0;
			int z = axis == Axis.Z ? distance : 0;
			mut.move(x, y, z);
		}
	}
	

}
