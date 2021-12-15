package com.unascribed.yttr.crafting;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShatteringRecipe implements Recipe<CraftingInventory> {
	private final Identifier id;
	private final String group;
	private final ItemStack output;
	private final Ingredient input;

	public ShatteringRecipe(Identifier id, String group, ItemStack output, Ingredient input) {
		this.id = id;
		this.group = group;
		this.output = output;
		this.input = input;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.SHATTERING;
	}
	
	@Override
	public RecipeType<?> getType() {
		return YRecipeTypes.SHATTERING;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.ofSize(1, input);
	}

	@Override
	public boolean matches(CraftingInventory craftingInventory, World world) {
		RecipeMatcher recipeMatcher = new RecipeMatcher();
		int i = 0;

		for(int j = 0; j < craftingInventory.size(); ++j) {
			ItemStack itemStack = craftingInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				++i;
				recipeMatcher.method_20478(itemStack, 1);
			}
		}

		return i == 1 && recipeMatcher.match(this, null);
	}

	@Override
	public ItemStack craft(CraftingInventory craftingInventory) {
		return output.copy();
	}
	
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height) {
		return width > 0 && height > 0;
	}

	public static class Serializer implements RecipeSerializer<ShatteringRecipe> {
		@Override
		public ShatteringRecipe read(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient input = Ingredient.fromJson(jsonObject.get("ingredient"));
			ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
			return new ShatteringRecipe(identifier, string, itemStack, input);
		}

		@Override
		public ShatteringRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient input = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShatteringRecipe(identifier, string, itemStack, input);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf, ShatteringRecipe recipe) {
			packetByteBuf.writeString(recipe.group);
			recipe.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(recipe.output);
		}
	}
}
