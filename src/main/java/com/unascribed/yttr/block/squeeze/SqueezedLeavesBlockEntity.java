package com.unascribed.yttr.block.squeeze;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.unascribed.yttr.init.YBlockEntities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SqueezedLeavesBlockEntity extends BlockEntity {

	public int decayTime;
	
	public long squeezeBegin = -1;
	public boolean finished;
	
	private final BlockPos.Mutable tmp = new BlockPos.Mutable();
	
	private int y = Integer.MIN_VALUE;
	
	private final List<BlockPos> queue = Lists.newArrayList();
	private final List<BlockPos> queueQueue = Lists.newArrayList();
	private final Set<BlockPos> scannedThisLayer = Sets.newHashSet();
	
	public SqueezedLeavesBlockEntity() {
		super(YBlockEntities.SQUEEZED_LEAVES);
	}
	
	public boolean step() {
		decayTime = 0;
		for (int i = 0; i < 100; i++) {
			if (stepInner()) return true;
		}
		return false;
	}
	
	private boolean stepInner() {
		if (y == Integer.MIN_VALUE) {
			tmp.set(pos);
			int lowestEligibleY = tmp.getY();
			for (int y = tmp.getY(); y >= 0; y--) {
				tmp.setY(y);
				if (tmp.equals(pos) || canFill(tmp)) {
					lowestEligibleY = y;
				} else {
					break;
				}
			}
			tmp.setY(lowestEligibleY);
			queue.add(tmp.toImmutable());
			y = lowestEligibleY;
			scannedThisLayer.clear();
			markDirty();
		}
		if (scannedThisLayer.size() > 4096) {
			queue.clear();
			queueQueue.clear();
		}
		if (queue.isEmpty()) {
			if (!queueQueue.isEmpty()) {
				queue.addAll(queueQueue);
				queueQueue.clear();
			} else {
				if (y < pos.getY()) {
					y++;
					markDirty();
					queue.add(new BlockPos(pos.getX(), y, pos.getZ()));
					scannedThisLayer.clear();
				} else if (world.getBlockState(pos.up()).getMaterial().isReplaceable()) {
					world.setBlockState(pos.up(), getCachedState());
					world.setBlockState(pos, Blocks.WATER.getDefaultState());
				} else {
					finished = true;
				}
			}
			return false;
		}
		Iterator<BlockPos> iter = queue.iterator();
		while (iter.hasNext()) {
			BlockPos bp = iter.next();
			scannedThisLayer.add(bp);
			iter.remove();
			boolean filled = fill(bp);
			if (filled || bp.equals(pos) || world.getBlockState(bp).isOf(Blocks.WATER)) {
				for (Direction d : Direction.Type.HORIZONTAL) {
					tmp.set(bp).move(d);
					if (canFill(tmp)) {
						queueQueue.add(tmp.toImmutable());
					}
				}
				if (filled) {
					return true;
				}
			} else if (!iter.hasNext()) {
				finished = true;
			}
		}
		return false;
	}
	
	private boolean canFill(BlockPos pos) {
		BlockState bs = world.getBlockState(pos);
		FluidState fs = bs.getFluidState();
		return !scannedThisLayer.contains(pos)
				&& (fs.isEmpty() || fs.isIn(FluidTags.WATER))
				&& (bs.getMaterial().isReplaceable() || (bs.getBlock() instanceof FluidFillable && ((FluidFillable)bs.getBlock()).canFillWithFluid(world, pos, bs, Fluids.WATER)));
	}
	
	private boolean fill(BlockPos pos) {
		BlockState bs = world.getBlockState(pos);
		FluidState fs = bs.getFluidState();
		if (fs.isEmpty() || fs.isIn(FluidTags.WATER)) {
			if (bs.getBlock() instanceof FluidFillable) {
				return ((FluidFillable)bs.getBlock()).tryFillWithFluid(world, pos, bs, Fluids.WATER.getDefaultState());
			} else if (bs.getMaterial().isReplaceable()) {
				return world.setBlockState(pos, Blocks.WATER.getDefaultState());
			}
		}
		return false;
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag.putInt("DecayTime", decayTime);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		decayTime = tag.getInt("DecayTime");
	}
	

}
