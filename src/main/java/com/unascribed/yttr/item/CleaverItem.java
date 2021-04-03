package com.unascribed.yttr.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.NBTUtils;
import com.unascribed.yttr.block.entity.CleavedBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.math.partitioner.Plane;
import com.unascribed.yttr.math.partitioner.Polygon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class CleaverItem extends Item {

	public static final int SUBDIVISIONS = 4;
	
	public CleaverItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		if (!requiresSneaking() || ctx.getPlayer().isSneaking()) {
			BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
			if (state.isSolidBlock(ctx.getWorld(), ctx.getBlockPos()) && state.getOutlineShape(ctx.getWorld(), ctx.getBlockPos()) == VoxelShapes.fullCube()) {
				Vec3d point = findCutPoint(ctx.getHitPos().subtract(Vec3d.of(ctx.getBlockPos())));
				if (point == null) return ActionResult.CONSUME;
				setCleaveBlock(ctx.getStack(), ctx.getBlockPos());
				setCleaveStart(ctx.getStack(), point);
				ctx.getPlayer().setCurrentHand(ctx.getHand());
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.PASS;
	}
	
	private Vec3d findCutPoint(Vec3d hit) {
		double x = (Math.round(hit.x*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double y = (Math.round(hit.y*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double z = (Math.round(hit.z*SUBDIVISIONS))/(double)SUBDIVISIONS;
		double dist = hit.squaredDistanceTo(x, y, z);
		if (dist > 0.1*0.1) return null;
		if (x < 0 || y < 0 || z < 0 || x > 1 || y > 1 || z > 1) return null;
		return new Vec3d(x, y, z);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		user.swingHand(user.getActiveHand());
		if (world.isClient) return;
		
		BlockPos block = getCleaveBlock(stack);
		Vec3d start = getCleaveStart(stack);
		setCleaveBlock(stack, null);
		setCleaveStart(stack, null);
		
		BlockState state = world.getBlockState(block);
		if (!state.isSolidBlock(world, block) || state.getOutlineShape(world, block) != VoxelShapes.fullCube()) return;
		
		HitResult hr = user.raycast(2, 1, false);
		if (hr.getType() == Type.BLOCK) {
			BlockHitResult bhr = (BlockHitResult)hr;
			Vec3d end = findCutPoint(bhr.getPos().subtract(Vec3d.of(block)));
			if (end == null) return;
			System.out.println(start+" - "+end);
			Plane plane = new Plane(start.subtract(end).normalize(), -0.5);
			List<Polygon> above = Lists.newArrayList();
			List<Polygon> on = Lists.newArrayList();
			List<Polygon> below = Lists.newArrayList();
			for (ImmutableList<Vec3d> polygon : CleavedBlockEntity.CUBE) {
				Polygon.split(new Polygon(polygon), plane, above, on, below);
			}
			System.out.println("above: "+above);
			System.out.println("on: "+on);
			System.out.println("below: "+below);
			if (!below.isEmpty()) {
				world.playSound(null, block, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1, 1.5f);
				if (state.getSoundGroup().getBreakSound() != null) {
					world.playSound(null, block, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1f);
				}
				world.setBlockState(block, YBlocks.CLEAVED_BLOCK.getDefaultState());
				List<List<Vec3d>> polygons = Lists.newArrayList();
				for (Polygon poly : below) {
					List<Vec3d> inner = Lists.newArrayList();
					Polygon.forEachDEdgeOfPoly(poly, (de) -> {
						System.out.println(de.srcPoint());
						inner.add(de.srcPoint());
					});
					polygons.add(inner);
				}
				((CleavedBlockEntity)world.getBlockEntity(block)).setPolygons(polygons);
				stack.damage(1, user, (e) -> user.sendToolBreakStatus(user.getActiveHand()));
			}
		}
		
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		setCleaveBlock(stack, null);
		setCleaveStart(stack, null);
		return stack;
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
		if (!stack.hasTag()) {
			if (pos == null) return;
			stack.setTag(new CompoundTag());
		}
		if (pos == null) {
			stack.getTag().remove("CleaveStart");
		} else {
			stack.getTag().put("CleaveStart", NBTUtils.vecToList(pos));
		}
	}
	
}
