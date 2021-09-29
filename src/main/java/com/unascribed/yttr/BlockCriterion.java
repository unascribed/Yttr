package com.unascribed.yttr;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockCriterion extends PlacedBlockCriterion {

	private final Identifier id;

	public BlockCriterion(String id) {
		this.id = new Identifier(id);
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
		BlockState state = player.getServerWorld().getBlockState(pos);
		trigger(player, pos, state, stack);
	}

	public void trigger(ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack stack) {
		test(player, cond -> cond.matches(state, pos, player.getServerWorld(), stack));
	}

	@Override
	public PlacedBlockCriterion.Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), (name) -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + name + ":");
			});
		}

		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new BlockCriterion.Conditions(extended, block, statePredicate, locationPredicate, itemPredicate);
	}

	@Nullable
	private static Block getBlock(JsonObject obj) {
		if (obj.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
			return Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> {
				return new JsonSyntaxException("Unknown block type '" + identifier + "'");
			});
		} else {
			return null;
		}
	}

	public class Conditions extends PlacedBlockCriterion.Conditions {
		public Conditions(EntityPredicate.Extended player, @Nullable Block block, StatePredicate state, LocationPredicate location, ItemPredicate item) {
			super(player, block, state, location, item);
		}

		@Override
		public Identifier getId() {
			return BlockCriterion.this.getId();
		}
	}
}
