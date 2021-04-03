package com.unascribed.yttr.block.entity;

import com.unascribed.yttr.NBTUtils;
import com.unascribed.yttr.init.YBlockEntities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

	public static final ImmutableList<ImmutableList<Vec3d>> CUBE = ImmutableList.of(
			ImmutableList.of(
				new Vec3d(0, 0, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(1, 0, 1),
				new Vec3d(0, 0, 1)
			),
			
			ImmutableList.of(
				new Vec3d(0, 1, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 1, 1),
				new Vec3d(0, 1, 1)
			),
			
			ImmutableList.of(
				new Vec3d(0, 0, 0),
				new Vec3d(0, 1, 0),
				new Vec3d(0, 1, 1),
				new Vec3d(0, 0, 1)
			),
			
			ImmutableList.of(
				new Vec3d(1, 0, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 1, 1),
				new Vec3d(1, 0, 1)
			),
			
			ImmutableList.of(
				new Vec3d(0, 0, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(0, 1, 0)
			),
			
			ImmutableList.of(
				new Vec3d(0, 0, 1),
				new Vec3d(1, 0, 1),
				new Vec3d(1, 1, 1),
				new Vec3d(0, 1, 1)
			)
			);
	
	private ImmutableList<ImmutableList<Vec3d>> polygons = CUBE;
	
	public CleavedBlockEntity() {
		super(YBlockEntities.CLEAVED_BLOCK);
	}
	
	public ImmutableList<ImmutableList<Vec3d>> getPolygons() {
		return polygons;
	}
	
	public void setPolygons(Iterable<? extends Iterable<Vec3d>> polygons) {
		this.polygons = ImmutableList.copyOf(Iterables.transform(polygons, ImmutableList::copyOf));
		markDirty();
		if (!world.isClient) sync();
	}

	// TODO this should be compacted, using NBT lists of doubles is inefficient
	
	public void fromTagInner(CompoundTag tag) {
		if (tag.contains("Polygons", NbtType.LIST)) {
			ImmutableList.Builder<ImmutableList<Vec3d>> builder = ImmutableList.builder();
			ListTag li = tag.getList("Polygons", NbtType.LIST);
			for (int i = 0; i < li.size(); i++) {
				ListTag inner = li.getList(i);
				ImmutableList.Builder<Vec3d> innerBuilder = ImmutableList.builder();
				for (int j = 0; j < inner.size(); j++) {
					innerBuilder.add(NBTUtils.listToVec(inner.getList(j)));
				}
				builder.add(innerBuilder.build());
			}
			polygons = builder.build();
		} else {
			polygons = CUBE;
		}
	}
	
	public CompoundTag toTagInner(CompoundTag tag) {
		ListTag li = new ListTag();
		for (ImmutableList<Vec3d> poly : polygons) {
			ListTag inner = new ListTag();
			for (Vec3d point : poly) {
				inner.add(NBTUtils.vecToList(point));
			}
			li.add(inner);
		}
		tag.put("Polygons", li);
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
