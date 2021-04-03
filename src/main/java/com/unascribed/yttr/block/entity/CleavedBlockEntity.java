package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.NBTUtils;
import com.unascribed.yttr.init.YBlockEntities;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

	public static final ImmutableList<Vec3d> CUBE = ImmutableList.of(
			new Vec3d(0, 0, 0),
			new Vec3d(1, 0, 0),
			new Vec3d(1, 0, 1),
			new Vec3d(0, 0, 1),
			
			new Vec3d(0, 1, 0),
			new Vec3d(1, 1, 0),
			new Vec3d(1, 1, 1),
			new Vec3d(0, 1, 1)
			);
	
	private ImmutableList<Vec3d> polygon = CUBE;
	
	public CleavedBlockEntity() {
		super(YBlockEntities.CLEAVED_BLOCK);
	}
	
	public ImmutableList<Vec3d> getPolygon() {
		return polygon;
	}
	
	public void setPolygon(ImmutableList<Vec3d> polygon) {
		this.polygon = polygon;
		markDirty();
		if (!world.isClient) sync();
	}

	public void fromTagInner(CompoundTag tag) {
		if (tag.contains("Polygon", NbtType.LIST)) {
			ImmutableList.Builder<Vec3d> builder = ImmutableList.builder();
			ListTag li = tag.getList("Polygon", NbtType.LIST);
			for (int i = 0; i < li.size(); i++) {
				builder.add(NBTUtils.listToVec(li.getList(i)));
			}
			polygon = builder.build();
		} else {
			polygon = CUBE;
		}
	}
	
	public CompoundTag toTagInner(CompoundTag tag) {
		ListTag li = new ListTag();
		for (Vec3d point : polygon) {
			li.add(NBTUtils.vecToList(point));
		}
		tag.put("Polygon", li);
		return tag;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		fromTagInner(tag);
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		toTagInner(tag);
		return super.toTag(tag);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTagInner(super.toInitialChunkDataTag());
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTagInner(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		toTagInner(tag);
		return tag;
	}
	

}
