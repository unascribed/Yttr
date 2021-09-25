package com.unascribed.yttr.content.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.unascribed.yttr.DelayedTask;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.mixin.accessor.AccessorBlockSoundGroup;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.util.math.partitioner.Plane;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class ShifterItem extends Item implements ItemColorProvider {

	public static float holderYaw = 0;
	public static boolean holderYawValid = false;
	
	public ShifterItem(Settings settings) {
		super(settings);
	}

	public void changeMode(ServerPlayerEntity player, boolean disconnected, boolean hidden, boolean plane) {
		ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
		boolean curDisconnected = stack.hasTag() && stack.getTag().getBoolean("ReplaceDisconnected");
		boolean curHidden = stack.hasTag() && stack.getTag().getBoolean("ReplaceHidden");
		boolean curPlane = stack.hasTag() && stack.getTag().getBoolean("PlaneRestrict");
		if (disconnected != curDisconnected) {
			player.sendMessage(new TranslatableText("tip.yttr.shifter.disconnected."+(disconnected ? "en" : "dis")+"abled"), true);
		}
		if (hidden != curHidden) {
			player.sendMessage(new TranslatableText("tip.yttr.shifter.hidden."+(hidden ? "en" : "dis")+"abled"), true);
		}
		if (plane != curPlane) {
			player.sendMessage(new TranslatableText("tip.yttr.shifter.plane."+(plane ? "en" : "dis")+"abled"), true);
		}
		if (!stack.hasTag()) stack.setTag(new NbtCompound());
		stack.getTag().putBoolean("ReplaceDisconnected", disconnected);
		stack.getTag().putBoolean("ReplaceHidden", hidden);
		stack.getTag().putBoolean("PlaneRestrict", plane);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getHand() == Hand.OFF_HAND) return ActionResult.PASS;
		ItemStack stack = context.getStack();
		ItemStack repl = context.getPlayer().getStackInHand(Hand.OFF_HAND);
		Set<BlockPos> blocks = getAffectedBlocks(context.getPlayer(), context.getWorld(), context.getBlockPos(), context.getSide(),
				stack.hasTag() && stack.getTag().getBoolean("ReplaceDisconnected"),
				stack.hasTag() && stack.getTag().getBoolean("ReplaceHidden"),
				stack.hasTag() && stack.getTag().getBoolean("PlaneRestrict"));
		scheduleMultiReplace(context.getPlayer(), context.getBlockPos(), context.getWorld(), repl.copy(), blocks);
		return ActionResult.SUCCESS;
	}
	
	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		ItemStack stack = miner.getStackInHand(Hand.MAIN_HAND);
		ItemStack repl = miner.getStackInHand(Hand.OFF_HAND);
		if (stack.hasTag() && repl.getItem() instanceof BlockItem) {
			stack.getTag().remove("UserIsConfusedCounter");
		}
		performReplacement(miner, pos, world, repl);
		return false;
	}
	
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		// causes all* blocks to be instamine and call canMine immediately
		return 10000000;
	}
	
	public void scheduleMultiReplace(PlayerEntity player, BlockPos center, World _world, ItemStack replacement, Set<BlockPos> positions) {
		if (!(_world instanceof ServerWorld)) return;
		ServerWorld world = (ServerWorld) _world;
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
	
	public void performReplacement(PlayerEntity player, BlockPos pos, World _world, ItemStack _replacement) {
		if (!(_world instanceof ServerWorld)) return;
		ServerWorld world = (ServerWorld) _world;
		if (_replacement.isEmpty()) return;
		if (_replacement.getItem() instanceof ReplicatorBlockItem) {
			_replacement = ReplicatorBlockItem.getHeldItem(_replacement);
		} else if (_replacement.getItem() instanceof CleaverItem) {
			CleaverItem ci = (CleaverItem)_replacement.getItem();
			Plane p = ci.getLastCut(_replacement);
			if (p != null && ci.performWorldCleave(world, pos, _replacement, player, p)) {
				return;
			}
		}
		if (_replacement.isEmpty()) return;
		ItemStack replacement = _replacement;
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
		if (curState.getHardness(world, pos) < 0) return;
		BlockSoundGroup curSg = curState.getSoundGroup();
		world.playSound(null, pos, ((AccessorBlockSoundGroup)curSg).yttr$getBreakSound(), SoundCategory.BLOCKS, ((curSg.getVolume()+1f)/2)*0.2f, curSg.getPitch()*0.8f);
		world.setBlockState(pos, curState.getFluidState().getBlockState(), 0, 0);
		BlockState refinedReplState = b.getPlacementState(new ItemPlacementContext(player, Hand.OFF_HAND, replacement, bhr));
		if (refinedReplState != null) {
			replState = refinedReplState;
		}
		BlockSoundGroup replSg = replState.getSoundGroup();
		world.playSound(null, pos, replSg.getPlaceSound(), SoundCategory.BLOCKS, ((replSg.getVolume()+1f)/2)*0.2f, replSg.getPitch()*0.8f);
		if (!player.isCreative()) {
			if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
				for (ItemStack is : drops) {
					double xo = world.random.nextFloat() * 0.5F + 0.25D;
					double yo = world.random.nextFloat() * 0.5F + 0.25D;
					double zo = world.random.nextFloat() * 0.5F + 0.25D;
					ItemEntity ie = new ItemEntity(world, pos.getX() + xo, pos.getY() + yo, pos.getZ() + zo, is);
					ie.setPickupDelay(0);
					world.spawnEntity(ie);
					ie.onPlayerCollision(player);
					if (ie.getStack().isEmpty()) {
						ie.remove();
					} else {
						ie.setToDefaultPickupDelay();
					}
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

	public Set<BlockPos> getAffectedBlocks(PlayerEntity player, World world, BlockPos start, Direction face,
			boolean includeDisconnected, boolean includeHidden, boolean planeRestrict) {
		BlockState sample = world.getBlockState(start);
		if (sample.isAir()) return Collections.emptySet();
		Iterable<Direction> directions = Arrays.asList(Direction.values());
		if (planeRestrict) {
			Axis axisZ = face.getAxis();
			List<Axis> axes = Arrays.asList(Direction.Axis.values());
			Axis axisX = Iterables.find(axes, a -> a != axisZ);
			Axis axisY = Iterables.find(Lists.reverse(axes), a -> a != axisZ);
			directions = Iterables.filter(directions, d -> d.getAxis() == axisX || d.getAxis() == axisY);
		}
		BlockPos corner1 = start;
		BlockPos corner2 = start;
		for (Direction d : directions) {
			if (d.getDirection() == AxisDirection.NEGATIVE) {
				corner1 = corner1.offset(d, 4);
			} else {
				corner2 = corner2.offset(d, 4);
			}
		}
		if (includeDisconnected) {
			return StreamSupport.stream(BlockPos.iterate(corner1, corner2).spliterator(), false)
					.filter(bp -> world.getBlockState(bp) == sample)
					.filter(bp -> world.getBlockState(bp).getHardness(world, bp) >= 0)
					.filter(bp -> includeHidden || !isHidden(world, bp))
					.map(BlockPos::toImmutable)
					.collect(Collectors.toSet());
		} else {
			Box box = new Box(corner1.getX(), corner1.getY(), corner1.getZ(), corner2.getX()+1, corner2.getY()+1, corner2.getZ()+1);
			Set<BlockPos> seen = Sets.newHashSet();
			Set<BlockPos> scan = Sets.newHashSet();
			Set<BlockPos> nextScan = Sets.newHashSet();
			int i = 0;
			scan.add(start);
			seen.add(start);
			while (!scan.isEmpty()) {
				if (i++ > 768) break;
				for (BlockPos bp : scan) {
					for (Direction d : directions) {
						BlockPos c = bp.offset(d);
						if (!box.contains(c.getX()+0.5, c.getY()+0.5, c.getZ()+0.5)) continue;
						if (!includeHidden && isHidden(world, c)) continue;
						BlockState bs2 = world.getBlockState(c);
						if (bs2 == sample && seen.add(c)) {
							nextScan.add(c);
						}
					}
				}
				scan.clear();
				scan.addAll(nextScan);
				nextScan.clear();
			}
			if (!includeHidden) seen.removeIf(bp -> isHidden(world, bp));
			return seen;
		}
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
