package com.unascribed.yttr;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NBTUtils {

	public static ListTag vecToList(Vec3d vec) {
		ListTag li = new ListTag();
		li.add(DoubleTag.of(vec.x));
		li.add(DoubleTag.of(vec.y));
		li.add(DoubleTag.of(vec.z));
		return li;
	}

	static @Nullable
	public Vec3d listToVec(ListTag li) {
		if (li.getElementType() != NbtType.DOUBLE) return null;
		if (li.size() != 3) return null;
		return new Vec3d(li.getDouble(0), li.getDouble(1), li.getDouble(2));
	}

	public static ListTag blockPosToList(BlockPos vec) {
		ListTag li = new ListTag();
		li.add(IntTag.of(vec.getX()));
		li.add(IntTag.of(vec.getY()));
		li.add(IntTag.of(vec.getZ()));
		return li;
	}

	static @Nullable
	public BlockPos listToBlockPos(ListTag li) {
		if (li.getElementType() != NbtType.INT) return null;
		if (li.size() != 3) return null;
		return new BlockPos(li.getInt(0), li.getInt(1), li.getInt(2));
	}

}
