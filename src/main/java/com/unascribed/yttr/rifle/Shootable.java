package com.unascribed.yttr.rifle;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Shootable {

	/**
	 * @return {@code true} to prevent default logic
	 */
	boolean onShotByRifle(World world, BlockState bs, LivingEntity user, RifleMode mode, float power, BlockPos pos, BlockHitResult bhr);
	
}
