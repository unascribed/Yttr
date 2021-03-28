package com.unascribed.yttr;

import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonObject;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient.Entry;
import net.minecraft.util.registry.Registry;

public class EntityIngredientEntry implements Entry {

	public final EntityType<?> entityType;
	
	public EntityIngredientEntry(EntityType<?> entityType) {
		this.entityType = entityType;
	}

	@Override
	public Collection<ItemStack> getStacks() {
		ItemStack is = new ItemStack(Yttr.SNARE);
		is.getOrCreateSubTag("Contents").putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
		return Collections.singleton(is);
	}

	@Override
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		return obj;
	}

}
