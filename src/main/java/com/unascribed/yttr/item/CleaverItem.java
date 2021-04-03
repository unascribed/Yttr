package com.unascribed.yttr.item;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
			if (state.isSolidBlock(ctx.getWorld(), ctx.getBlockPos())) {
				float hX = (float)(ctx.getHitPos().x-ctx.getBlockPos().getX());
				float hY = (float)(ctx.getHitPos().y-ctx.getBlockPos().getY());
				float hZ = (float)(ctx.getHitPos().z-ctx.getBlockPos().getZ());
				float x = (Math.round(hX*SUBDIVISIONS))/(float)SUBDIVISIONS;
				float y = (Math.round(hY*SUBDIVISIONS))/(float)SUBDIVISIONS;
				float z = (Math.round(hZ*SUBDIVISIONS))/(float)SUBDIVISIONS;
				float dX = hX-x;
				float dY = hY-y;
				float dZ = hZ-z;
				float dist = (dX*dX)+(dY*dY)+(dZ*dZ);
				if (dist > 0.1*0.1) return ActionResult.CONSUME;
				setCleaveBlock(ctx.getStack(), ctx.getBlockPos());
				setCleaveStart(ctx.getStack(), new Vec3d(hX, hY, hZ));
				ctx.getPlayer().setCurrentHand(ctx.getHand());
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		user.swingHand(user.getActiveHand());
		setCleaveBlock(stack, null);
		setCleaveStart(stack, null);
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
	
	private static ListTag vecToList(Vec3d vec) {
		ListTag li = new ListTag();
		li.add(DoubleTag.of(vec.x));
		li.add(DoubleTag.of(vec.y));
		li.add(DoubleTag.of(vec.z));
		return li;
	}
	
	private static @Nullable Vec3d listToVec(ListTag li) {
		if (li.getElementType() != NbtType.DOUBLE) return null;
		if (li.size() != 3) return null;
		return new Vec3d(li.getDouble(0), li.getDouble(1), li.getDouble(2));
	}
	
	private static ListTag blockPosToList(BlockPos vec) {
		ListTag li = new ListTag();
		li.add(IntTag.of(vec.getX()));
		li.add(IntTag.of(vec.getY()));
		li.add(IntTag.of(vec.getZ()));
		return li;
	}
	
	private static @Nullable BlockPos listToBlockPos(ListTag li) {
		if (li.getElementType() != NbtType.INT) return null;
		if (li.size() != 3) return null;
		return new BlockPos(li.getInt(0), li.getInt(1), li.getInt(2));
	}
	
	public @Nullable BlockPos getCleaveBlock(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("CleaveBlock", NbtType.LIST)) {
			return listToBlockPos(stack.getTag().getList("CleaveBlock", NbtType.INT));
		}
		return null;
	}
	
	public @Nullable Vec3d getCleaveStart(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("CleaveStart", NbtType.LIST)) {
			return listToVec(stack.getTag().getList("CleaveStart", NbtType.DOUBLE));
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
			stack.getTag().put("CleaveBlock", blockPosToList(pos));
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
			stack.getTag().put("CleaveStart", vecToList(pos));
		}
	}
	
}
