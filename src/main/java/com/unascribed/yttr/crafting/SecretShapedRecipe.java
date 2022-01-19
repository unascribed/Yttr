package com.unascribed.yttr.crafting;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.mixin.accessor.AccessorShapedRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class SecretShapedRecipe extends ShapedRecipe {

	public SecretShapedRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
		super(id, group, width, height, input, output);
	}
	
	public SecretShapedRecipe(ShapedRecipe copy) {
		this(copy.getId(), ((AccessorShapedRecipe)copy).yttr$getGroup(), copy.getWidth(), copy.getHeight(), copy.getIngredients(), copy.getOutput());
	}
	
	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.of();
	}
	
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.SECRET_CRAFTING_SHAPED;
	}
	
	public static class Serializer extends ShapedRecipe.Serializer {
		
		@Override
		public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
			return new SecretShapedRecipe(super.read(identifier, jsonObject));
		}
		
		@Override
		public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			return new SecretShapedRecipe(super.read(identifier, packetByteBuf));
		}
		
		@Override
		public void write(PacketByteBuf packetByteBuf, ShapedRecipe shapedRecipe) {
			super.write(packetByteBuf, shapedRecipe);
		}
		
	}
	
}
