package com.unascribed.yttr.crafting;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.unascribed.yttr.crafting.ingredient.FluidIngredient;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.init.YRecipeTypes;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SoakingRecipe implements Recipe<Inventory> {

	protected final Identifier id;
	protected final String group;
	protected final DefaultedList<Ingredient> ingredients;
	protected final FluidIngredient catalyst;
	protected final Either<ItemStack, BlockState> result;
	protected final int time;
	protected final int multiDelay;
	protected final @Nullable SoundEvent sound;

	public SoakingRecipe(Identifier id, String group, List<Ingredient> ingredients, FluidIngredient catalyst, Either<ItemStack, BlockState> result, int time, int multiDelay, SoundEvent sound) {
		this.id = id;
		this.group = group;
		this.ingredients = DefaultedList.copyOf(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
		this.catalyst = catalyst;
		this.result = result;
		this.time = time;
		this.multiDelay = multiDelay;
		this.sound = sound;
	}

	public FluidIngredient getCatalyst() {
		return catalyst;
	}

	public int getTime() {
		return time;
	}
	
	public int getMultiDelay() {
		return multiDelay;
	}
	
	public @Nullable SoundEvent getSound() {
		return sound;
	}
	
	public Either<ItemStack, BlockState> getResult() {
		return result;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return getOutput().copy();
	}
	
	@Override
	public ItemStack getOutput() {
		return result.map(is -> is, bs -> new ItemStack(bs.getBlock()));
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		return ingredients;
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

	@Override
	public RecipeType<?> getType() {
		return YRecipeTypes.SOAKING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.SOAKING;
	}
	
	public static class Serializer implements RecipeSerializer<SoakingRecipe> {

		@Override
		public SoakingRecipe read(Identifier id, JsonObject obj) {
			String group = JsonHelper.getString(obj, "group", "");
			List<Ingredient> ingredients = StreamSupport.stream(obj.get("ingredients").getAsJsonArray().spliterator(), false)
						.map(Ingredient::fromJson)
						.collect(Collectors.toList());
			FluidIngredient catalyst = FluidIngredient.fromJson(obj.get("catalyst"));
			Either<ItemStack, BlockState> result;
			JsonObject resultJson = obj.getAsJsonObject("result");
			if (resultJson.has("item")) {
				result = Either.left(ShapedRecipe.getItemStack(resultJson));
			} else {
				BlockArgumentParser bap = new BlockArgumentParser(new StringReader(resultJson.get("block").getAsString()), false);
				try {
					bap.parse(false);
				} catch (CommandSyntaxException e) {
					throw new RuntimeException(e);
				}
				result = Either.right(bap.getBlockState());
			}
			int time = JsonHelper.getInt(obj, "time", 0);
			int multiDelay = JsonHelper.getInt(obj, "multiDelay", 1);
			String soundId = JsonHelper.getString(obj, "sound", null);
			SoundEvent sound = null;
			if (soundId != null) {
				sound = Registry.SOUND_EVENT.get(new Identifier(soundId));
			}
			return new SoakingRecipe(id, group, ingredients, catalyst, result, time, multiDelay, sound);
		}

		@Override
		public SoakingRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString();
			int ingredientCount = buf.readVarInt();
			List<Ingredient> ingredients = Lists.newArrayListWithCapacity(ingredientCount);
			for (int i = 0; i < ingredientCount; i++) {
				ingredients.add(Ingredient.fromPacket(buf));
			}
			FluidIngredient catalyst = FluidIngredient.read(buf);
			Either<ItemStack, BlockState> result;
			if (buf.readBoolean()) {
				result = Either.left(buf.readItemStack());
			} else {
				result = Either.right(Block.getStateFromRawId(buf.readVarInt()));
			}
			int time = buf.readVarInt();
			int multiDelay = buf.readVarInt();
			SoundEvent sound = null;
			if (buf.readBoolean()) {
				sound = Registry.SOUND_EVENT.get(buf.readVarInt());
			}
			return new SoakingRecipe(id, group, ingredients, catalyst, result, time, multiDelay, sound);
		}

		@Override
		public void write(PacketByteBuf buf, SoakingRecipe recipe) {
			buf.writeString(recipe.group);
			buf.writeVarInt(recipe.ingredients.size());
			recipe.ingredients.forEach(i -> i.write(buf));
			recipe.catalyst.write(buf);
			buf.writeBoolean(recipe.result.left().isPresent());
			recipe.getResult()
				.ifLeft(buf::writeItemStack)
				.ifRight(bs -> buf.writeVarInt(Block.STATE_IDS.getRawId(bs)));
			buf.writeVarInt(recipe.time);
			buf.writeVarInt(recipe.multiDelay);
			buf.writeBoolean(recipe.sound != null);
			if (recipe.sound != null) buf.writeVarInt(Registry.SOUND_EVENT.getRawId(recipe.sound));
		}
		
	}

}
