package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.entity.DiscOfContinuityEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class YEntityTypes {

	public static final EntityType<DiscOfContinuityEntity> DISC_OF_CONTINUITY = EntityType.Builder.<DiscOfContinuityEntity>create(DiscOfContinuityEntity::new, SpawnGroup.MISC)
			.setDimensions(0.8f, 0.2f)
			.build("yttr:disc_of_continuity");
	
	public static void init() {
		Yttr.autoRegister(Registry.ENTITY_TYPE, YEntityTypes.class, EntityType.class);
	}
	
}
