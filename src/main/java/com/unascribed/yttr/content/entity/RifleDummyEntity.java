package com.unascribed.yttr.content.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class RifleDummyEntity extends Entity {

	public static final EntityType<RifleDummyEntity> TYPE = EntityType.Builder.<RifleDummyEntity>create(SpawnGroup.MISC)
			.disableSaving()
			.disableSummon()
			.build("yttr:rifle_dummy");
	
	public RifleDummyEntity(World world) {
		super(TYPE, world);
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	public Packet<?> createSpawnPacket() {
		throw new UnsupportedOperationException();
	}

}
