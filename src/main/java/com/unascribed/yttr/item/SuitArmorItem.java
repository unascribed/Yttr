package com.unascribed.yttr.item;

import java.util.UUID;

import com.unascribed.yttr.FakeEntityAttribute;
import com.unascribed.yttr.SuitResource;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class SuitArmorItem extends ArmorItem {

	private static final UUID[] MODIFIERS = {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
	
	public SuitArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.putAll(super.getAttributeModifiers(slot));
		UUID id = MODIFIERS[slot.getEntitySlotId()];
		// super only does this for netherite armor for some reason, even though armormaterial has a getter for it...
		builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(id, "Armor knockback resistance", this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(id, "Armor penalty", -0.4, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		builder.put(EntityAttributes.GENERIC_FLYING_SPEED, new EntityAttributeModifier(id, "Armor penalty", -0.4, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(id, "Armor penalty", -0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(id, "Armor penalty", -0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		builder.put(new FakeEntityAttribute("attribute.name.yttr.jump_height"), new EntityAttributeModifier(id, "Armor penalty", -0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		this.attributeModifiers = builder.build();
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == this.slot ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}
	
	public int getResourceAmount(ItemStack stack, SuitResource resource) {
//		setResourceAmount(stack, resource, resource.getMaximum());
		CompoundTag resources = stack.getSubTag("Resources");
		if (resources == null || !resources.contains(resource.name())) return resource.getDefaultAmount();
		return resources.getInt(resource.name());
	}
	
	public void setResourceAmount(ItemStack stack, SuitResource resource, int amount) {
		stack.getOrCreateSubTag("Resources").putInt(resource.name(), amount);
	}
	
	public void consumeResource(ItemStack stack, SuitResource resource, int amount) {
		if (amount <= 0) return;
		System.out.println("consume "+amount+" "+resource);
		int amt = getResourceAmount(stack, resource);
		amt -= amount;
		setResourceAmount(stack, resource, Math.max(0, amt));
	}

}
