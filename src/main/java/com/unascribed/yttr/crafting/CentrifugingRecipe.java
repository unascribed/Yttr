package com.unascribed.yttr.crafting;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

public class CentrifugingRecipe implements Recipe<Inventory> {

	protected final Identifier id;
	protected final String group;
	protected final Ingredient input;
	protected final int inputCount;
	protected final ImmutableList<ItemStack> outputs;
	protected final int spinTime;

	public CentrifugingRecipe(Identifier id, String group, Ingredient input, int inputCount, List<ItemStack> outputs, int spinTime) {
		this.id = id;
		this.group = group;
		this.input = input;
		this.inputCount = inputCount;
		this.outputs = ImmutableList.copyOf(outputs);
		this.spinTime = spinTime;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return input.test(inv.getStack(0)) && inv.getStack(0).getCount() >= inputCount && canFitOutput(inv);
	}

	@Override
	public ItemStack craft(Inventory inv) {
		if (!canFitOutput(inv)) return ItemStack.EMPTY;
		inv.removeStack(0, inputCount);
		for (int i = 0; i < outputs.size(); i++) {
			ItemStack out = outputs.get(i);
			ItemStack cur = inv.getStack(i+1);
			if (cur.isEmpty()) {
				inv.setStack(i+1, out.copy());
			} else {
				// we already know this stack's type matches and that there's enough room from canFitOutput
				cur.increment(out.getCount());
				inv.setStack(i+1, cur);
			}
		}
		return outputs.get(0).copy();
	}

	public boolean canFitOutput(Inventory inv) {
		for (int i = 0; i < outputs.size(); i++) {
			ItemStack out = outputs.get(i);
			ItemStack cur = inv.getStack(i+1);
			if (cur.isEmpty()) continue;
			if (!ItemStack.areItemsEqual(out, cur) || !ItemStack.areTagsEqual(out, cur)) return false;
			if (cur.getCount()+out.getCount() > cur.getMaxCount()) return false;
		}
		return true;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> out = DefaultedList.of();
		out.add(input);
		return out;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}
	
	public ImmutableList<ItemStack> getOutputs() {
		return outputs;
	}

	@Override
	public String getGroup() {
		return group;
	}

	public int getSpinTime() {
		return spinTime;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return YRecipeTypes.CENTRIFUGING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.CENTRIFUGING;
	}
	
	public static class Serializer implements RecipeSerializer<CentrifugingRecipe> {

		@Override
		public CentrifugingRecipe read(Identifier id, JsonObject obj) {
			String group = JsonHelper.getString(obj, "group", "");
			Ingredient ingredient = Ingredient.fromJson(obj.get("ingredient"));
			int inputCount = 1;
			if (obj.get("ingredient") instanceof JsonObject) {
				JsonObject ingobj = obj.getAsJsonObject("ingredient");
				if (ingobj.has("count")) {
					inputCount = ingobj.getAsJsonPrimitive("count").getAsInt();
				}
			}
			JsonArray resultsJson = obj.getAsJsonArray("results");
			if (resultsJson.size() == 0) throw new IllegalArgumentException("A centrifuging recipe must have at least 1 output");
			if (resultsJson.size() > 4) throw new IllegalArgumentException("A centrifuging recipe can only have up to 4 outputs");
			List<ItemStack> results = Lists.newArrayList();
			for (JsonElement je : resultsJson) {
				results.add(ShapedRecipe.getItemStack(je.getAsJsonObject()));
			}
			int time = JsonHelper.getInt(obj, "time", 400);
			return new CentrifugingRecipe(id, group, ingredient, inputCount, results, time);
		}

		@Override
		public CentrifugingRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString(32767);
			Ingredient ingredient = Ingredient.fromPacket(buf);
			int inputCount = buf.readUnsignedByte();
			int outputsCount = buf.readUnsignedByte();
			List<ItemStack> outputs = Lists.newArrayList();
			for (int i = 0; i < outputsCount; i++) {
				outputs.add(buf.readItemStack());
			}
			int time = buf.readVarInt();
			return new CentrifugingRecipe(id, group, ingredient, inputCount, outputs, time);
		}

		@Override
		public void write(PacketByteBuf buf, CentrifugingRecipe recipe) {
			buf.writeString(recipe.group);
			recipe.input.write(buf);
			buf.writeByte(recipe.inputCount);
			buf.writeByte(recipe.outputs.size());
			for (ItemStack is : recipe.outputs) {
				buf.writeItemStack(is);
			}
			buf.writeVarInt(recipe.spinTime);
		}
		
	}

}
