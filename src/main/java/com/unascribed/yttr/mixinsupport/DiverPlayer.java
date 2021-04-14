package com.unascribed.yttr.mixinsupport;

import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.util.math.Vec2i;

import net.minecraft.util.math.BlockPos;

public interface DiverPlayer {

	boolean yttr$isDiving();
	void yttr$setDiving(boolean b);
	
	boolean yttr$isInvisibleFromDiving();
	boolean yttr$isNoGravityFromDiving();
	
	Set<UUID> yttr$getKnownGeysers();
	
	int yttr$getLastDivePosUpdate();
	void yttr$setLastDivePosUpdate(int i);
	
	@Nullable Vec2i yttr$getDivePos();
	void yttr$setDivePos(@Nullable Vec2i v);
	
	int yttr$getFastDiveTime();
	void yttr$setFastDiveTime(int i);
	
	@Nullable BlockPos yttr$getFastDiveTarget();
	void yttr$setFastDiveTarget(@Nullable BlockPos g);
	
}
