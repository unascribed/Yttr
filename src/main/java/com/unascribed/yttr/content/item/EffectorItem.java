package com.unascribed.yttr.content.item;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixinsupport.YttrWorld;
import com.unascribed.yttr.network.MessageS2CEffectorHole;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;

public class EffectorItem extends Item {

	public static final int MAX_FUEL = 2048;
	
	public EffectorItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (getFuel(stack) >= MAX_FUEL) return TypedActionResult.pass(stack);
		BlockHitResult hr = raycast(world, user, FluidHandling.SOURCE_ONLY);
		if (hr.getType() == Type.BLOCK) {
			BlockState bs = world.getBlockState(hr.getBlockPos());
			if (bs.getBlock() instanceof FluidDrainable && bs.getFluidState().getFluid().isIn(YTags.Fluid.VOID)) {
				Fluid fluid = ((FluidDrainable)bs.getBlock()).tryDrainFluid(world, hr.getBlockPos(), bs);
				if (fluid.isIn(YTags.Fluid.VOID)) {
					user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1, 1);
					if (world.isClient) return TypedActionResult.success(stack, true);
					setFuel(stack, MAX_FUEL);
					return TypedActionResult.success(stack, false);
				}
			}
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockHitResult hr = raycast(world, context.getPlayer(), FluidHandling.SOURCE_ONLY);
		if (hr.getType() == Type.BLOCK) {
			FluidState fs = world.getFluidState(hr.getBlockPos());
			if (fs.isIn(YTags.Fluid.VOID) && fs.isStill()) return ActionResult.PASS;
		}
		if (!(world instanceof ServerWorld)) return ActionResult.SUCCESS;
		BlockPos pos = context.getBlockPos();
		Direction dir = context.getSide().getOpposite();
		ItemStack stack = context.getStack();
		int fuel = context.getPlayer().abilities.creativeMode ? MAX_FUEL : getFuel(stack);
		if (fuel <= 0) {
			context.getPlayer().sendMessage(new TranslatableText("tip.yttr.effector.no_fuel"), true);
			return ActionResult.FAIL;
		}
		int amt = effect(world, pos, dir, stack, context.getPlayer().getUuid(), Math.min(fuel, 32), true);
		YStats.add(context.getPlayer(), YStats.BLOCKS_EFFECTED, amt*100);
		if (context.getPlayer() instanceof ServerPlayerEntity) {
			YCriteria.EFFECT_BLOCK.trigger((ServerPlayerEntity)context.getPlayer(), pos, stack);
		}
		if (!context.getPlayer().abilities.creativeMode) setFuel(stack, fuel-amt);
		new MessageS2CEffectorHole(pos, dir, amt).sendToAllWatching(context.getPlayer());
		return ActionResult.SUCCESS;
	}
	
	public int getFuel(ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getInt("Fuel") : 0;
	}
	
	public void setFuel(ItemStack stack, int fuel) {
		if (!stack.hasTag()) stack.setTag(new NbtCompound());
		stack.getTag().putInt("Fuel", fuel);
	}

	public interface RenderUpdateCallback {
		void scheduleRenderUpdate(int x, int y, int z);
	}
	
	public static int effect(World world, BlockPos pos, Direction dir, @Nullable ItemStack stack, @Nullable UUID owner, int distance, boolean server) {
		if (!(world instanceof YttrWorld)) return 0;
		YttrWorld ew = (YttrWorld)world;
		BlockPos.Mutable cursor = pos.mutableCopy();
		BlockPos.Mutable outerCursor = new BlockPos.Mutable();
		Axis axisZ = dir.getAxis();
		List<Axis> axes = Arrays.asList(Direction.Axis.values());
		Axis axisX = Iterables.find(axes, a -> a != axisZ);
		Axis axisY = Iterables.find(Lists.reverse(axes), a -> a != axisZ);
		int hits = -2;
		for (int z = -2; z < distance; z++) {
			cursor.set(pos).move(dir, z);
			boolean everythingWasUnpassable = true;
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					outerCursor.set(cursor);
					move(outerCursor, axisX, x);
					move(outerCursor, axisY, y);
					BlockState bs = world.getBlockState(outerCursor);
					if (bs.getHardness(world, outerCursor) < 0) continue;
					if (!bs.isAir()) everythingWasUnpassable = false;
					ew.yttr$addPhaseBlock(outerCursor, 150, 0, owner);
				}
			}
			if (z >= 0 && server && everythingWasUnpassable) {
				break;
			}
			hits++;
		}
		return hits;
	}

	public static void move(BlockPos.Mutable mut, Axis axis, int distance) {
		if (distance != 0) {
			int x = axis == Axis.X ? distance : 0;
			int y = axis == Axis.Y ? distance : 0;
			int z = axis == Axis.Z ? distance : 0;
			mut.move(x, y, z);
		}
	}
	

}
