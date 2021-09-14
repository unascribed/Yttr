package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.render.DiscOfContinuityRenderer;
import com.unascribed.yttr.content.entity.DiscOfContinuityEntity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class YEntities {

	@Renderer(DiscOfContinuityRenderer.class)
	public static final EntityType<DiscOfContinuityEntity> DISC_OF_CONTINUITY = EntityType.Builder.<DiscOfContinuityEntity>create(DiscOfContinuityEntity::new, SpawnGroup.MISC)
			.setDimensions(0.8f, 0.2f)
			.build("yttr:disc_of_continuity");
	
	public static void init() {
		Yttr.autoRegister(Registry.ENTITY_TYPE, YEntities.class, EntityType.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Renderer {
		Class<? extends EntityRenderer<?>> value();
	}
	
}
