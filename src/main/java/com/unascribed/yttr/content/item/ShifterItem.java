package com.unascribed.yttr.content.item;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.unascribed.yttr.DelayedTask;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.YLog;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class ShifterItem extends Item implements ItemColorProvider {

	public static float holderYaw = 0;
	public static boolean holderYawValid = false;
	
	public ShifterItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (user.isSneaking()) {
			if (!stack.hasTag()) stack.setTag(new NbtCompound());
			boolean hidden = stack.getTag().getBoolean("ReplaceHidden");
			stack.getTag().putBoolean("ReplaceHidden", !hidden);
			if (hidden) {
				user.sendMessage(new TranslatableText("tip.yttr.shifter.hidden.disabled"), true);
			} else {
				user.sendMessage(new TranslatableText("tip.yttr.shifter.hidden.enabled"), true);
			}
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getHand() == Hand.OFF_HAND) return ActionResult.PASS;
		if (context.getPlayer().isSneaking()) {
			return use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
		}
		Set<BlockPos> blocks = getAffectedBlocks(context.getPlayer(), context.getWorld(), context.getBlockPos(), context.getStack().hasTag() && context.getStack().getTag().getBoolean("ReplaceHidden"));
		scheduleMultiReplace(context.getPlayer(), context.getBlockPos(), context.getWorld(), blocks);
		return ActionResult.SUCCESS;
	}
	
	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		performReplacement(miner, pos, world, miner.getStackInHand(Hand.OFF_HAND));
		return false;
	}
	
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		// causes all* blocks to be instamine and call canMine immediately
		return 10000000;
	}
	
	public void scheduleMultiReplace(PlayerEntity player, BlockPos center, World _world, Set<BlockPos> positions) {
		if (!(_world instanceof ServerWorld)) return;
		ServerWorld world = (ServerWorld) _world;
		ItemStack replacement = player.getStackInHand(Hand.OFF_HAND).copy();
		Multiset<Integer> delays = HashMultiset.create();
		for (BlockPos pos : positions) {
			int delay = pos.equals(center) ? 0 : (int) ((MathHelper.sqrt(pos.getSquaredDistance(center)*10))+RANDOM.nextInt(4));
			while (delays.count(delay) > 4) {
				delay++;
			}
			delays.add(delay);
			Yttr.delayedServerTasks.add(new DelayedTask(delay, () -> {
				performReplacement(player, pos, world, replacement);
			}));
		}
	}
	
	public void performReplacement(PlayerEntity player, BlockPos pos, World _world, ItemStack replacement) {
		if (!(_world instanceof ServerWorld)) return;
		ServerWorld world = (ServerWorld) _world;
		if (replacement.isEmpty()) return;
		BlockState curState = world.getBlockState(pos);
		if (curState.isAir()) return;
		int consumed = Inventories.remove(player.inventory, (is) -> ItemStack.areItemsEqual(is, replacement) && ItemStack.areTagsEqual(is, replacement), 1, true);
		if (consumed == 0) return;
		Item i = replacement.getItem();
		if (!(i instanceof BlockItem)) return;
		Block b = ((BlockItem)i).getBlock();
		BlockHitResult bhr = new BlockHitResult(new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5), Direction.UP, pos, true);
		BlockState replState = b.getPlacementState(new ItemPlacementContext(player, Hand.OFF_HAND, replacement, bhr));
		if (replState == null) return;
		if (replState == curState) return;
		if (!replState.canPlaceAt(world, pos)) return;
		List<ItemStack> drops = Block.getDroppedStacks(curState, world, pos, curState.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null);
		if (!world.breakBlock(pos, false, player)) return;
		BlockState refinedReplState = b.getPlacementState(new ItemPlacementContext(player, Hand.OFF_HAND, replacement, bhr));
		if (refinedReplState != null) {
			replState = refinedReplState;
		}
		if (!player.isCreative()) {
			for (ItemStack is : drops) {
				player.inventory.insertStack(is);
				if (is.getCount() > 0) {
					Block.dropStack(world, pos, is);
				}
			}
			int rm = Inventories.remove(player.inventory, (is) -> ItemStack.areItemsEqual(is, replacement) && ItemStack.areTagsEqual(is, replacement), 1, false);
			if (rm == 0) {
				YLog.warn("Couldn't consume a replacement item after verifying it with a dry run?? Forcefully decrementing off-hand stack!");
				ItemStack off = player.getStackInHand(Hand.OFF_HAND);
				off.decrement(1);
				player.setStackInHand(Hand.OFF_HAND, off);
			}
		}
		world.setBlockState(pos, replState);
		world.spawnParticles(ParticleTypes.CRIT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.5, 0.5, 0.5, 0.05);
		world.spawnParticles(ParticleTypes.FIREWORK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.5, 0.5, 0.5, 0.05);
	}

	public Set<BlockPos> getAffectedBlocks(PlayerEntity player, World world, BlockPos start, boolean includeHidden) {
		BlockState sample = world.getBlockState(start);
		if (sample.isAir()) return Collections.emptySet();
		return StreamSupport.stream(BlockPos.iterate(start.add(-4, -4, -4), start.add(4, 4, 4)).spliterator(), false)
				.filter(bp -> world.getBlockState(bp) == sample)
				.filter(bp -> world.getBlockState(bp).getHardness(world, bp) >= 0)
				.filter(bp -> includeHidden || !isHidden(world, bp))
				.map(BlockPos::toImmutable)
				.collect(Collectors.toSet());
	}
	
	public static boolean isHidden(World world, BlockPos bp) {
		BlockPos.Mutable mut = bp.mutableCopy();
		for (Direction d : Direction.values()) {
			mut.set(bp).move(d);
			if (!world.getBlockState(mut).isSideSolidFullSquare(world, mut, d.getOpposite())) {
				return false;
			}
		}
		return true;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return -1;
		float yaw;
		if (holderYawValid) {
			yaw = holderYaw;
		} else if (MinecraftClient.getInstance().player != null) {
			yaw = MinecraftClient.getInstance().player.yaw;
		} else {
			yaw = 0;
		}
		yaw = MathHelper.wrapDegrees(yaw)+180;
		if (tintIndex == 1) {
			yaw -= 100;
		} else if (tintIndex == 2) {
			yaw += 100;
		}
		float hue = (yaw%360)/360f;
		if (hue < 0) hue += 1;
		return MathHelper.hsvToRgb(hue, 0.3f, 1.0f);
	}

}
