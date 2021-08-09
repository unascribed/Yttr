package com.unascribed.yttr.mechanics;

import java.util.List;

import com.unascribed.yttr.crafting.PistonSmashingRecipe;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SmashCloudLogic {

	public static final String MAGIC = "§e§6§eYttrSmashCloud/";
	
	public static PistonSmashingRecipe consumeGasCloud(World world, Box box) {
		List<AreaEffectCloudEntity> clouds = world.getEntitiesByClass(AreaEffectCloudEntity.class, box, (cloud) ->
				cloud != null && cloud.isAlive() && cloud.getName().asString().startsWith(MAGIC) && cloud.getRadius() > 0);
		if (!clouds.isEmpty()) {
			AreaEffectCloudEntity cloud = clouds.get(0);
			Identifier id = Identifier.tryParse(cloud.getName().asString().substring(MAGIC.length()));
			if (id != null) {
				PistonSmashingRecipe r = (PistonSmashingRecipe) world.getRecipeManager().get(id).filter(o -> o instanceof PistonSmashingRecipe).orElse(null);
				if (r != null && !r.getCloudOutput().isEmpty()) {
					cloud.setRadius(cloud.getRadius() - 0.25f);
					if (cloud.getRadius() < 0.25f) cloud.remove();
					Vec3d center = box.getCenter();
					world.playSound(null, center.x, center.y, center.z, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1, 1);
					return r;
				}
			}
		}
		return null;
	}

}
