package com.unascribed.yttr;

import java.util.List;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GlowingGasLogic {

	public static boolean consumeGasCloud(World world, Box box) {
		List<AreaEffectCloudEntity> clouds = world.getEntitiesByClass(AreaEffectCloudEntity.class, box, (cloud) ->
		cloud != null && cloud.isAlive() && cloud.getName().asString().equals("§e§6§eGlowdampCloud") && cloud.getRadius() > 0);
		if (!clouds.isEmpty()) {
			AreaEffectCloudEntity cloud = clouds.get(0);
			cloud.setRadius(cloud.getRadius() - 0.3f);
			if (cloud.getRadius() <= 0) cloud.remove();
			Vec3d center = box.getCenter();
			world.playSound(null, center.x, center.y, center.z, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1, 1);
			return true;
		}
		return false;
	}

}
