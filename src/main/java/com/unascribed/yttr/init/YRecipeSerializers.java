package com.unascribed.yttr.init;

import com.google.gson.JsonObject;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.LampRecipe;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YRecipeSerializers {

	public static final ShapedRecipe.Serializer LAMP_CRAFTING = new ShapedRecipe.Serializer() {
		@Override
		public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
			return new LampRecipe(super.read(identifier, jsonObject));
		}
		
		@Override
		public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			return new LampRecipe(super.read(identifier, packetByteBuf));
		}
	};

	public static void init() {
		Yttr.autoRegister(Registry.RECIPE_SERIALIZER, YRecipeSerializers.class, RecipeSerializer.class);
	}
	
}
