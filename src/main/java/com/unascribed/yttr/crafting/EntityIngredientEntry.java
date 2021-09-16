package com.unascribed.yttr.crafting;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YItems;

import com.google.common.base.Ascii;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient.Entry;
import net.minecraft.util.Arm;
import net.minecraft.util.registry.Registry;

public class EntityIngredientEntry implements Entry {

	public final EntityType<?> entityType;
	public final @Nullable Arm mainHand;
	
	public EntityIngredientEntry(EntityType<?> entityType, @Nullable Arm mainHand) {
		this.entityType = entityType;
		this.mainHand = mainHand;
	}

	@Override
	public Collection<ItemStack> getStacks() {
		ItemStack is = new ItemStack(YItems.SNARE);
		is.getOrCreateSubTag("Contents").putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
		if (mainHand != null) {
			NbtList lore = new NbtList();
			lore.add(NbtString.of("\"§7"+(mainHand == Arm.LEFT ? "Left" : "Right")+"-Handed\""));
			is.getOrCreateSubTag("display").put("Lore", lore);
		}
		return Collections.singleton(is);
	}

	@Override
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("yttr:entity", Registry.ENTITY_TYPE.getId(entityType).toString());
		if (mainHand != null) {
			obj.addProperty("yttr:main_hand", Ascii.toLowerCase(mainHand.name()));
		}
		return obj;
	}

}
