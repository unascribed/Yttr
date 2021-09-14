package com.unascribed.yttr.content.entity;

import com.unascribed.yttr.init.YEntities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DiscOfContinuityEntity extends ItemEntity {

	public DiscOfContinuityEntity(EntityType<? extends ItemEntity> entityType, World world) {
		super(entityType, world);
	}

	public DiscOfContinuityEntity(World world, double x, double y, double z, ItemStack stack) {
		this(world, x, y, z);
		this.setStack(stack);
	}

	public DiscOfContinuityEntity(World world, double x, double y, double z) {
		this(YEntities.DISC_OF_CONTINUITY, world);
		this.updatePosition(x, y, z);
		this.yaw = this.random.nextFloat() * 360.0F;
		this.setVelocity(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
	}

	
	
}
