package com.unascribed.yttr.item;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Attackable;
import com.unascribed.yttr.NBTUtils;
import com.unascribed.yttr.block.entity.CleavedBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.math.partitioner.DEdge;
import com.unascribed.yttr.math.partitioner.Plane;
import com.unascribed.yttr.math.partitioner.Polygon;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

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
		return false;
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
				if (!state.isSolidBlock(world, pos) || state.getOutlineShape(world, pos) != VoxelShapes.fullCube()) return ActionResult.FAIL;
				Vec3d point = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(pos)));
				if (point == null) return ActionResult.FAIL;
				setCleaveCorner(stack, null);
				setCleaveBlock(stack, pos);
				setCleaveStart(stack, point);
				return ActionResult.SUCCESS;
			} else if (getCleaveCorner(stack) == null) {
				Vec3d point = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(pos)));
				if (point == null) return ActionResult.FAIL;
				setCleaveCorner(stack, point);
				return ActionResult.SUCCESS;
			} else {
				if (world.isClient) return ActionResult.CONSUME;
				
				BlockPos block = getCleaveBlock(stack);
				Vec3d start = getCleaveStart(stack);
				Vec3d corner = getCleaveCorner(stack);
				setCleaveBlock(stack, null);
				setCleaveStart(stack, null);
				setCleaveCorner(stack, null);
				
				BlockState state = world.getBlockState(block);
				if (!state.isSolidBlock(world, block) || state.getOutlineShape(world, block) != VoxelShapes.fullCube()) return ActionResult.FAIL;
				
				HitResult hr = player.raycast(2, 1, false);
				if (hr.getType() == Type.BLOCK) {
					BlockHitResult bhr = (BlockHitResult)hr;
					Vec3d end = findCutPoint(bhr.getPos().subtract(Vec3d.of(block)));
					if (end == null) return ActionResult.FAIL;
					System.out.println(start+" - "+corner+" - "+end);
					List<Polygon> result = performCleave(start, corner, end, CleavedBlockEntity.cube(), false);
					if (!result.isEmpty()) {
						world.playSound(null, block, YSounds.CLEAVER, SoundCategory.BLOCKS, 1, 1.5f);
						if (state.getSoundGroup().getBreakSound() != null) {
							world.playSound(null, block, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1f);
						}
						world.setBlockState(block, YBlocks.CLEAVED_BLOCK.getDefaultState());
						CleavedBlockEntity cbe = ((CleavedBlockEntity)world.getBlockEntity(block));
						cbe.setDonor(state);
						cbe.setPolygons(result);
						stack.damage(1, player, (e) -> player.sendToolBreakStatus(Hand.MAIN_HAND));
					}
				}
			}
		}
		return ActionResult.PASS;
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
		if (getCleaveCorner(stack) != null) {
			setCleaveCorner(stack, null);
		} else {
			setCleaveBlock(stack, null);
			setCleaveStart(stack, null);
		}
	}
	
	public static List<Polygon> performCleave(Vec3d start, Vec3d corner, Vec3d end, List<Polygon> polygonsIn, boolean invert) {
		Plane plane = new Plane(start, corner, end);
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
	
	public void setCleaveBlock(ItemStack stack, @Nullable BlockPos pos) {
		if (!stack.hasTag()) {
			if (pos == null) return;
			stack.setTag(new CompoundTag());
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

	private void setVec(ItemStack stack, String key, @Nullable Vec3d pos) {
		if (!stack.hasTag()) {
			if (pos == null) return;
			stack.setTag(new CompoundTag());
		}
		if (pos == null) {
			stack.getTag().remove(key);
		} else {
			stack.getTag().put(key, NBTUtils.vecToList(pos));
		}
	}
	
}
