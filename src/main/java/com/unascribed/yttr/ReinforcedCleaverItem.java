package com.unascribed.yttr;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedCleaverItem extends CleaverItem implements DynamicAttributeTool {

	private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
			.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 8, EntityAttributeModifier.Operation.ADDITION))
			.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.2, EntityAttributeModifier.Operation.ADDITION))
			.build();
	
	public ReinforcedCleaverItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		StatusEffectInstance cur = target.getStatusEffect(Yttr.BLEEDING);
		int armorAmount = 0;
		for (ItemStack armor : target.getArmorItems()) {
			if (!armor.isEmpty()) armorAmount++;
		}
		int duration = 5*20;
		if (cur != null) {
			duration += cur.getDuration();
		}
		if (duration > 20*20) duration = 20*20;
		if (cur == null || attacker.world.random.nextInt(8)-2 > armorAmount) {
			int level = cur == null ? 0 : Math.min(5, cur.getAmplifier()+1);
			target.addStatusEffect(new StatusEffectInstance(Yttr.BLEEDING, duration, level, false, false, true));
		} else if (attacker.world.random.nextInt(3) == 0) {
			target.addStatusEffect(new StatusEffectInstance(Yttr.BLEEDING, duration, cur.getAmplifier(), false, false, true));
		}
		for (EquipmentSlot es : EquipmentSlot.values()) {
			if (es.getType() == Type.ARMOR && attacker.world.random.nextInt(5) == 0) {
				target.getEquippedStack(es).damage(4, target, (e) -> target.sendEquipmentBreakStatus(es));
			}
		}
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		stack.damage(1, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}
	
	@Override
	public int getMiningLevel(ItemStack stack, @Nullable LivingEntity user) {
		return 1;
	}
	
	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		return 1;
	}
	
	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		return tag == FabricToolTags.SWORDS ? 6.5f : 5.5f;
	}
	
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return 5.5f;
	}
	
	@Override
	public boolean isEffectiveOn(BlockState state) {
		return true;
	}
	
	@Override
	public int getEnchantability() {
		return 17;
	}
	
	@Override
	public boolean requiresSneaking() {
		return true;
	}
	
}
