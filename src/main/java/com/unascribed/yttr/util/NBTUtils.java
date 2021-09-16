package com.unascribed.yttr.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NBTUtils {

	public static NbtList vecToList(Vec3d vec) {
		NbtList li = new NbtList();
		li.add(NbtDouble.of(vec.x));
		li.add(NbtDouble.of(vec.y));
		li.add(NbtDouble.of(vec.z));
		return li;
	}

	public static @Nullable Vec3d listToVec(NbtList li) {
		if (li.getHeldType() != NbtType.DOUBLE) return null;
		if (li.size() != 3) return null;
		return new Vec3d(li.getDouble(0), li.getDouble(1), li.getDouble(2));
	}

	public static NbtList blockPosToList(BlockPos vec) {
		NbtList li = new NbtList();
		li.add(NbtInt.of(vec.getX()));
		li.add(NbtInt.of(vec.getY()));
		li.add(NbtInt.of(vec.getZ()));
		return li;
	}

	public static @Nullable BlockPos listToBlockPos(NbtList li) {
		if (li.getHeldType() != NbtType.INT) return null;
		if (li.size() != 3) return null;
		return new BlockPos(li.getInt(0), li.getInt(1), li.getInt(2));
	}

}
