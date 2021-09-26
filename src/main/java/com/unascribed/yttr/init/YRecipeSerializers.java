package com.unascribed.yttr.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.crafting.SoakingRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YRecipeSerializers {

	public static final ShapedRecipe.Serializer LAMP_CRAFTING = new ShapedRecipe.Serializer() {
		@Override
		public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
			LampRecipe lr = new LampRecipe(super.read(identifier, jsonObject));
			if (jsonObject.has("yttr:strip_tags")) {
				for (JsonElement je : jsonObject.get("yttr:strip_tags").getAsJsonArray()) {
					lr.addStripTag(je.getAsString());
				}
			}
			return lr;
		}
		
		@Override
		public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			LampRecipe lr = new LampRecipe(super.read(identifier, packetByteBuf));
			if (packetByteBuf.isReadable()) {
				int count = packetByteBuf.readVarInt();
				for (int i = 0; i < count; i++) {
					lr.addStripTag(packetByteBuf.readString(32767));
				}
			}
			return lr;
		}
		
		@Override
		public void write(PacketByteBuf packetByteBuf, ShapedRecipe shapedRecipe) {
			super.write(packetByteBuf, shapedRecipe);
			if (shapedRecipe instanceof LampRecipe) {
				LampRecipe lr = (LampRecipe)shapedRecipe;
				packetByteBuf.writeVarInt(lr.getStripTags().size());
				for (String tag : lr.getStripTags()) {
					packetByteBuf.writeString(tag);
				}
			}
		}
	};
	
	public static final CentrifugingRecipe.Serializer CENTRIFUGING = new CentrifugingRecipe.Serializer();
	public static final VoidFilteringRecipe.Serializer VOID_FILTERING = new VoidFilteringRecipe.Serializer();
	public static final PistonSmashingRecipe.Serializer PISTON_SMASHING = new PistonSmashingRecipe.Serializer();
	public static final SoakingRecipe.Serializer SOAKING = new SoakingRecipe.Serializer();

	public static void init() {
		Yttr.autoRegister(Registry.RECIPE_SERIALIZER, YRecipeSerializers.class, RecipeSerializer.class);
	}
	
}
