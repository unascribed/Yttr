package com.unascribed.yttr.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.minecraft.entity.EquipmentSlot;

public class EquipmentSlots {

	public static final ImmutableList<EquipmentSlot> ALL = ImmutableList.copyOf(EquipmentSlot.values());
	public static final ImmutableList<EquipmentSlot> ARMOR = ImmutableList.copyOf(Iterables.filter(ALL, es -> es.getType() == EquipmentSlot.Type.ARMOR));
	public static final ImmutableList<EquipmentSlot> HAND = ImmutableList.copyOf(Iterables.filter(ALL, es -> es.getType() == EquipmentSlot.Type.HAND));

}
