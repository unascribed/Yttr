package com.unascribed.yttr.content.effect;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BleedingStatusEffect extends StatusEffect {
	private static final DamageSource SOURCE = new DamageSource("yttr.bleeding") {{
		setBypassesArmor();
		setUnblockable();
	}};
	
	public BleedingStatusEffect(StatusEffectType type, int color) {
		super(type, color);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		boolean hurt = false;
		if (entity.age % Math.max(1, 40-(amplifier*5)) == 0) {
			hurt = true;
			int oldHurtTime = entity.hurtTime;
			entity.hurtTime = -20;
			entity.damage(SOURCE, (amplifier/4)+1);
			entity.hurtTime = oldHurtTime;
		}
		World w = entity.getEntityWorld();
		if (w instanceof ServerWorld) {
			ServerWorld sw = (ServerWorld)w;
			if (hurt || entity.getRandom().nextInt(Math.max(10-(amplifier*2), 1)) == 0) {
				Box box = entity.getBoundingBox();
				Vec3d center = box.getCenter();
				sw.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(YItems.ULTRAPURE_CINNABAR)), center.getX(), center.getY(), center.getZ(), (amplifier+1)*2, box.getXLength()/3, box.getYLength()/3, box.getZLength()/3, 0);
			}
		}
	}
	
}