package com.unascribed.yttr.enchant;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class VorpalEnchantment extends Enchantment {

	public VorpalEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMinPower(int level) {
		return 20 + 10 * (level-1);
	}
	
	@Override
	public int getMaxPower(int level) {
		return getMinPower(level)+50;
	}
	
	@Override
	public boolean isTreasure() {
		return true;
	}
	
	@Override
	public int getMaxLevel() {
		return 4;
	}
	
	private LivingEntity lastAttacker;
	private int lastAttackerAttackTime;
	
	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		// in a display of sheer brilliance, this method is called twice for enchants on held items
		if (lastAttacker == user && user.getLastAttackTime() == lastAttackerAttackTime) return;
		lastAttacker = user;
		lastAttackerAttackTime = user.getLastAttackTime();
		if (ThreadLocalRandom.current().nextInt(100) < level*level) {
			target.damage(new EntityDamageSource("yttr.vorpal", user), 100);
			if (user instanceof ServerPlayerEntity) {
				Box b = target.getBoundingBox();
				Vec3d c = b.getCenter();
				ParticleS2CPacket pkt = new ParticleS2CPacket(ParticleTypes.INSTANT_EFFECT, false, c.x, c.y, c.z,
						(float)b.getXLength()/2, (float)b.getYLength()/2, (float)b.getZLength()/2,
						0, 20);
				((ServerPlayerEntity)user).networkHandler.sendPacket(pkt);
			}
		}
	}
	
}
