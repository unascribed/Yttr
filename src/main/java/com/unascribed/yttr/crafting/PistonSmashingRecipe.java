package com.unascribed.yttr.crafting;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;

import com.google.common.collect.Lists;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PistonSmashingRecipe implements Recipe<Inventory> {

	protected final Identifier id;
	protected final String group;
	protected final BlockIngredient input;
	protected final BlockIngredient catalyst;
	protected final ItemStack output;
	protected final boolean hasCloud;
	protected final int cloudColor;
	protected final int cloudSize;
	protected final ItemStack cloudOutput;
	protected final List<StatusEffectInstance> cloudEffects;

	public PistonSmashingRecipe(Identifier id, String group, BlockIngredient input, BlockIngredient catalyst, ItemStack output, boolean hasCloud, int cloudColor, int cloudSize, ItemStack cloudOutput, List<StatusEffectInstance> cloudEffects) {
		this.id = id;
		this.group = group;
		this.input = input;
		this.catalyst = catalyst;
		this.output = output;
		this.hasCloud = hasCloud;
		this.cloudColor = cloudColor;
		this.cloudSize = cloudSize;
		this.cloudOutput = cloudOutput;
		this.cloudEffects = cloudEffects;
	}
	
	public BlockIngredient getInput() {
		return input;
	}
	
	public BlockIngredient getCatalyst() {
		return catalyst;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}
	
	public boolean hasCloud() {
		return hasCloud;
	}
	
	public int getCloudColor() {
		return cloudColor;
	}
	
	public int getCloudSize() {
		return cloudSize;
	}
	
	public ItemStack getCloudOutput() {
		return cloudOutput;
	}
	
	public List<StatusEffectInstance> getCloudEffects() {
		return cloudEffects;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		return DefaultedList.of();
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
	public RecipeType<?> getType() {
		return YRecipeTypes.PISTON_SMASHING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.PISTON_SMASHING;
	}
	
	public static class Serializer implements RecipeSerializer<PistonSmashingRecipe> {

		@Override
		public PistonSmashingRecipe read(Identifier id, JsonObject obj) {
			String group = JsonHelper.getString(obj, "group", "");
			BlockIngredient input = BlockIngredient.fromJson(obj.get("input"));
			BlockIngredient catalyst = BlockIngredient.fromJson(obj.get("catalysts"));
			ItemStack output = obj.has("output") ? ShapedRecipe.getItemStack(obj.getAsJsonObject("output")) : ItemStack.EMPTY;
			boolean hasCloud = obj.has("cloud");
			int cloudColor = 0;
			int cloudSize = 0;
			ItemStack cloudOutput = ItemStack.EMPTY;
			List<StatusEffectInstance> cloudEffects = Lists.newArrayList();
			if (hasCloud) {
				JsonObject cloud = obj.getAsJsonObject("cloud");
				cloudColor = Integer.parseInt(cloud.get("color").getAsString().substring(1), 16);
				cloudSize = JsonHelper.getInt(cloud, "size", 1);
				if (cloud.has("output")) {
					cloudOutput = ShapedRecipe.getItemStack(cloud.getAsJsonObject("output"));
				}
				if (cloud.has("effects")) {
					Iterable<JsonElement> iter;
					if (cloud.get("effects").isJsonObject()) {
						iter = Collections.singleton(cloud.get("effects"));
					} else {
						iter = cloud.get("effects").getAsJsonArray();
					}
					for (JsonElement ele : iter) {
						JsonObject enObj = ele.getAsJsonObject();
						StatusEffect effect = Registry.STATUS_EFFECT.get(Identifier.tryParse(enObj.get("effect").getAsString()));
						if (effect == null) continue;
						int amplifier = JsonHelper.getInt(enObj, "amplifier", 0);
						int duration = JsonHelper.getInt(enObj, "duration");
						cloudEffects.add(new StatusEffectInstance(effect, duration, amplifier));
					}
				}
			}
			return new PistonSmashingRecipe(id, group, input, catalyst, output, hasCloud, cloudColor, cloudSize, cloudOutput, cloudEffects);
		}

		@Override
		public PistonSmashingRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString(32767);
			BlockIngredient input = BlockIngredient.read(buf);
			BlockIngredient catalyst = BlockIngredient.read(buf);
			ItemStack output = buf.readItemStack();
			boolean hasCloud = buf.readBoolean();
			int cloudColor = 0;
			int cloudSize = 0;
			ItemStack cloudOutput = ItemStack.EMPTY;
			List<StatusEffectInstance> cloudEffects = Lists.newArrayList();
			if (hasCloud) {
				cloudColor = buf.readMedium();
				cloudSize = buf.readVarInt();
				cloudOutput = buf.readItemStack();
				int cloudEffectCount = buf.readVarInt();
				for (int i = 0; i < cloudEffectCount; i++) {
					cloudEffects.add(new StatusEffectInstance(StatusEffect.byRawId(buf.readVarInt()), buf.readVarInt(), buf.readVarInt()));
				}
			}
			return new PistonSmashingRecipe(id, group, input, catalyst, output, hasCloud, cloudColor, cloudSize, cloudOutput, cloudEffects);
		}

		@Override
		public void write(PacketByteBuf buf, PistonSmashingRecipe recipe) {
			buf.writeString(recipe.group);
			recipe.input.write(buf);
			recipe.catalyst.write(buf);
			buf.writeItemStack(recipe.output);
			buf.writeBoolean(recipe.hasCloud);
			if (recipe.hasCloud) {
				buf.writeMedium(recipe.cloudColor);
				buf.writeVarInt(recipe.cloudSize);
				buf.writeItemStack(recipe.cloudOutput);
				buf.writeVarInt(recipe.cloudEffects.size());
				for (StatusEffectInstance sei : recipe.cloudEffects) {
					buf.writeVarInt(StatusEffect.getRawId(sei.getEffectType()));
					buf.writeVarInt(sei.getDuration());
					buf.writeVarInt(sei.getAmplifier());
				}
			}
		}
		
	}

}
