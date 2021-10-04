package com.unascribed.yttr.crafting;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class VoidFilteringRecipe implements Recipe<Inventory> {

	protected final Identifier id;
	protected final String group;
	protected final ItemStack output;
	protected final float chance;
	protected final boolean hidden;

	public VoidFilteringRecipe(Identifier id, String group, ItemStack output, float chance, boolean hidden) {
		this.id = id;
		this.group = group;
		this.output = output;
		this.chance = chance;
		this.hidden = hidden;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return output.copy();
	}

	public boolean canFitOutput(Inventory inv) {
		for (int i = 0; i < inv.size(); i++) {
			ItemStack cur = inv.getStack(i);
			if (cur.isEmpty() || (ItemStack.areItemsEqual(output, cur) && ItemStack.areTagsEqual(output, cur) && cur.getCount()+output.getCount() <= cur.getMaxCount())) return true;
		}
		return false;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return DefaultedList.of();
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}
	
	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
	
	public float getChance() {
		return chance;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public RecipeType<?> getType() {
		return YRecipeTypes.VOID_FILTERING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.VOID_FILTERING;
	}
	
	public static class Serializer implements RecipeSerializer<VoidFilteringRecipe> {

		@Override
		public VoidFilteringRecipe read(Identifier id, JsonObject obj) {
			String group = JsonHelper.getString(obj, "group", "");
			ItemStack output = ShapedRecipe.getItemStack(obj.getAsJsonObject("output"));
			float chance = JsonHelper.getFloat(obj, "chance");
			return new VoidFilteringRecipe(id, group, output, chance, JsonHelper.getBoolean(obj, "hidden", false));
		}

		@Override
		public VoidFilteringRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString(32767);
			ItemStack output = buf.readItemStack();
			float chance = buf.readFloat();
			boolean hidden = chance < 0;
			if (hidden) {
				chance = -chance;
			}
			return new VoidFilteringRecipe(id, group, output, chance, hidden);
		}

		@Override
		public void write(PacketByteBuf buf, VoidFilteringRecipe recipe) {
			buf.writeString(recipe.group);
			buf.writeItemStack(recipe.output);
			buf.writeFloat(recipe.chance*(recipe.hidden ? -1 : 1));
		}
		
	}

}
