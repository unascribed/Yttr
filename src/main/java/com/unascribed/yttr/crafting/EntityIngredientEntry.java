package com.unascribed.yttr.crafting;

import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YItems;

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
		ItemStack is = new ItemStack(YItems.SNARE);
		is.getOrCreateSubTag("Contents").putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
		return Collections.singleton(is);
	}

	@Override
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		return obj;
	}

}
