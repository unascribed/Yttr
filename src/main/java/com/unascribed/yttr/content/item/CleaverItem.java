package com.unascribed.yttr.content.item;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixin.accessor.AccessorBlockSoundGroup;
import com.unascribed.yttr.util.Attackable;
import com.unascribed.yttr.util.NBTUtils;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Plane;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;

public class CleaverItem extends Item implements Attackable {

	public static final int SUBDIVISIONS = 4;
	
	public CleaverItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == YItems.GLASSY_VOID;
	}
	
	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.abilities.creativeMode;
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		ItemStack stack = ctx.getStack();
		PlayerEntity player = ctx.getPlayer();
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		if (!requiresSneaking() || player.isSneaking()) {
			BlockPos expected = getCleaveBlock(stack);
			if (expected == null) {
				BlockState state = world.getBlockState(pos);
				if (!canCleave(world, pos, state)) return ActionResult.FAIL;
				Vec3d point = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(pos)));
				if (point == null) return ActionResult.FAIL;
				setCleaveCorner(stack, null);
				setCleaveBlock(stack, pos);
				setCleaveStart(stack, point);
				return ActionResult.SUCCESS;
			} else if (getCleaveCorner(stack) == null) {
				pos = getCleaveBlock(stack);
				Vec3d point = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(pos)));
				if (point == null) return ActionResult.FAIL;
				setCleaveCorner(stack, point);
				return ActionResult.SUCCESS;
			} else {
				if (world.isClient) return ActionResult.CONSUME;
				
				pos = getCleaveBlock(stack);
				Vec3d start = getCleaveStart(stack);
				Vec3d corner = getCleaveCorner(stack);
				
				BlockState state = world.getBlockState(pos);
				if (!canCleave(world, pos, state)) return ActionResult.FAIL;
				
				Vec3d end = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(pos)));
				if (end == null) return ActionResult.FAIL;
				Plane plane = new Plane(start, corner, end);
				if (performWorldCleave(world, pos, stack, player, plane)) {
					setLastCut(stack, plane);
					setCleaveBlock(stack, null);
					setCleaveStart(stack, null);
					setCleaveCorner(stack, null);
					player.sendMessage(new TranslatableText("tip.yttr.cleaver.repeat_cut"+(requiresSneaking() ? "_sneak" : "")), true);
				}
			}
		}
		return ActionResult.PASS;
	}

	public static boolean canCleave(World world, BlockPos pos, BlockState state) {
		// multi-cleaving brings out a lot of bugs in the renderer and partitioner. revisit later
		//if (state.isOf(YBlocks.CLEAVED_BLOCK)) return true;
		if (state.isIn(YTags.Block.UNCLEAVABLE)) return false;
		return !state.getBlock().hasBlockEntity() && state.getOutlineShape(world, pos) == VoxelShapes.fullCube() && state.getHardness(world, pos) >= 0;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (entity instanceof PlayerEntity && stack.hasTag()) {
			boolean wasSelected = stack.getTag().getBoolean("Selected");
			if (selected && !wasSelected) {
				if (stack.getTag().contains("LastCut")) {
					((PlayerEntity)entity).sendMessage(new TranslatableText("tip.yttr.cleaver.repeat_cut"+(requiresSneaking() ? "_sneak" : "")+".post"), true);
				}
				stack.getTag().putBoolean("Selected", true);
			} else if (!selected && wasSelected) {
				stack.getTag().putBoolean("Selected", false);
			}
		}
	}

	public static List<Polygon> getShape(World world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return Lists.newArrayList(Iterables.transform(((CleavedBlockEntity)be).getPolygons(), Polygon::copy));
		}
		return CleavedBlockEntity.cube();
	}

	private Vec3d findCutPoint(Vec3d hit) {
		double x = (Math.round(hit.x*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double y = (Math.round(hit.y*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double z = (Math.round(hit.z*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double dist = hit.squaredDistanceTo(x, y, z);
		if (dist > 0.15*0.15) return null;
		if (x < 0 || y < 0 || z < 0 || x > 1 || y > 1 || z > 1) return null;
		return new Vec3d(x, y, z);
	}

	@Override
	public void attack(PlayerEntity user) {
		ItemStack stack = user.getMainHandStack();
		if (getCleaveBlock(stack) == null) {
			if (requiresSneaking() && !user.isSneaking()) return;
			Plane p = getLastCut(stack);
			if (p != null) {
				BlockHitResult bhr = raycast(user.world, user, FluidHandling.NONE);
				if (bhr.getType() != Type.MISS && canCleave(user.world, bhr.getBlockPos(), user.world.getBlockState(bhr.getBlockPos()))) {
					if (performWorldCleave(user.world, bhr.getBlockPos(), stack, user, p)) {
						YStats.add(user, YStats.BLOCKS_CLEAVED, 1);
					}
				}
			}
		} else if (getCleaveCorner(stack) != null) {
			setCleaveCorner(stack, null);
		} else {
			setCleaveBlock(stack, null);
			setCleaveStart(stack, null);
		}
	}
	
	public boolean performWorldCleave(World world, BlockPos pos, ItemStack stack, PlayerEntity player, Plane plane) {
		BlockState state = world.getBlockState(pos);
		List<Polygon> shape = getShape(world, pos);
		List<Polygon> result = performCleave(plane, shape, false);
		if (!result.isEmpty()) {
			world.playSound(null, pos, YSounds.CLEAVER, SoundCategory.BLOCKS, 1, 1.5f);
			SoundEvent breakSound = ((AccessorBlockSoundGroup)state.getSoundGroup()).yttr$getBreakSound();
			if (breakSound != null) {
				world.playSound(null, pos, breakSound, SoundCategory.BLOCKS, 0.5f, 1f);
			}
			BlockEntity be = world.getBlockEntity(pos);
			CleavedBlockEntity cbe;
			if (be instanceof CleavedBlockEntity) {
				cbe = (CleavedBlockEntity)be;
			} else {
				BlockState newState = YBlocks.CLEAVED_BLOCK.getDefaultState().with(CleavedBlock.LUMINANCE, state.getLuminance());
				if (state.contains(Properties.WATERLOGGED)) {
					newState = newState.with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
				}
				world.setBlockState(pos, newState);
				cbe = ((CleavedBlockEntity)world.getBlockEntity(pos));
				cbe.setDonor(state);
			}
			cbe.setPolygons(result);
			stack.damage(1, player, (e) -> player.sendToolBreakStatus(Hand.MAIN_HAND));
			YStats.add(player, YStats.BLOCKS_CLEAVED, 1);
			if (player instanceof ServerPlayerEntity) {
				YCriteria.CLEAVE_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
			}
			return true;
		}
		return false;
	}
	
	public static List<Polygon> performCleave(Plane plane, List<Polygon> polygonsIn, boolean invert) {
		List<Polygon> above = Lists.newArrayList();
		List<Polygon> on = Lists.newArrayList();
		List<Polygon> below = Lists.newArrayList();
		for (Polygon polygon : polygonsIn) {
			Polygon.split(polygon, plane, above, on, below);
		}
		List<Polygon> out;
		if (invert) {
			out = Lists.newArrayList();
			out.addAll(on);
			out.addAll(below);
		} else {
			out = above;
		}
		if (out.isEmpty()) return out;
		if (out.size() < 3) return Collections.emptyList();
		
		List<Vec3d> joinerPoints = Lists.newArrayList();
		for (Polygon polygon : out) {
			for (DEdge de : polygon) {
				if (Math.abs(plane.sDistance(de.srcPoint())) < 1e-5) {
					joinerPoints.add(de.srcPoint());
				}
			}
		}
		if (!joinerPoints.isEmpty()) {
			Polygon p = new Polygon(Lists.reverse(joinerPoints));
			out.add(p);
		}

		if (out.equals(polygonsIn)) return Collections.emptyList();
		return out;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 36000;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}
	
	public boolean requiresSneaking() {
		return false;
	}
	
	public @Nullable BlockPos getCleaveBlock(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("CleaveBlock", NbtType.LIST)) {
			return NBTUtils.listToBlockPos(stack.getTag().getList("CleaveBlock", NbtType.INT));
		}
		return null;
	}
	
	public @Nullable Vec3d getCleaveStart(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("CleaveStart", NbtType.LIST)) {
			return NBTUtils.listToVec(stack.getTag().getList("CleaveStart", NbtType.DOUBLE));
		}
		return null;
	}
	
	public @Nullable Vec3d getCleaveCorner(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("CleaveCorner", NbtType.LIST)) {
			return NBTUtils.listToVec(stack.getTag().getList("CleaveCorner", NbtType.DOUBLE));
		}
		return null;
	}
	
	public @Nullable Plane getLastCut(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("LastCut", NbtType.COMPOUND)) {
			NbtCompound tag = stack.getTag().getCompound("LastCut");
			Vec3d normal = NBTUtils.listToVec(tag.getList("Normal", NbtType.DOUBLE));
			if (normal == null) return null;
			double distance = tag.getDouble("Distance");
			double epsilon = tag.getDouble("Epsilon");
			return new Plane(normal, distance, epsilon);
		}
		return null;
	}
	
	public void setCleaveBlock(ItemStack stack, @Nullable BlockPos pos) {
		if (!stack.hasTag()) {
			if (pos == null) return;
			stack.setTag(new NbtCompound());
		}
		if (pos == null) {
			stack.getTag().remove("CleaveBlock");
		} else {
			stack.getTag().put("CleaveBlock", NBTUtils.blockPosToList(pos));
		}
	}
	
	public void setCleaveStart(ItemStack stack, @Nullable Vec3d pos) {
		setVec(stack, "CleaveStart", pos);
	}
	
	public void setCleaveCorner(ItemStack stack, @Nullable Vec3d pos) {
		setVec(stack, "CleaveCorner", pos);
	}
	
	public void setLastCut(ItemStack stack, @Nullable Plane plane) {
		if (!stack.hasTag()) {
			if (plane == null) return;
			stack.setTag(new NbtCompound());
		}
		if (plane == null) {
			stack.getTag().remove("LastCut");
		} else {
			NbtCompound tag = new NbtCompound();
			tag.put("Normal", NBTUtils.vecToList(plane.normal()));
			tag.putDouble("Distance", plane.distance());
			tag.putDouble("Epsilon", plane.epsilon());
			stack.getTag().put("LastCut", tag);
		}
	}

	private void setVec(ItemStack stack, String key, @Nullable Vec3d pos) {
		if (!stack.hasTag()) {
			if (pos == null) return;
			stack.setTag(new NbtCompound());
		}
		if (pos == null) {
			stack.getTag().remove(key);
		} else {
			stack.getTag().put(key, NBTUtils.vecToList(pos));
		}
	}
	
}
