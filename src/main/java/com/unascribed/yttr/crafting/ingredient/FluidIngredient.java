package com.unascribed.yttr.crafting.ingredient;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidIngredient implements Predicate<Fluid> {

	private final Set<Fluid> exacts = Sets.newHashSet();
	private final Set<Tag<Fluid>> tags = Sets.newHashSet();
	
	private FluidIngredient() {}
	
	@Override
	public boolean test(Fluid b) {
		if (exacts.contains(b)) return true;
		for (Tag<Fluid> tag : tags) {
			if (b.isIn(tag)) return true;
		}
		return false;
	}
	
	public List<Fluid> getMatchingFluids() {
		List<Fluid> li = Lists.newArrayList();
		li.addAll(exacts);
		for (Tag<Fluid> tag : tags) {
			li.addAll(tag.values());
		}
		return li;
	}
	
	public void write(PacketByteBuf out) {
		List<Fluid> all = getMatchingFluids();
		out.writeVarInt(all.size());
		for (Fluid f : all) {
			out.writeVarInt(Registry.FLUID.getRawId(f));
		}
	}
	
	public static FluidIngredient read(PacketByteBuf in) {
		int amt = in.readVarInt();
		FluidIngredient out = new FluidIngredient();
		for (int i = 0; i < amt; i++) {
			out.exacts.add(Registry.FLUID.get(in.readVarInt()));
		}
		return out;
	}
	
	public static FluidIngredient fromJson(JsonElement ele) {
		FluidIngredient out = new FluidIngredient();
		if (ele.isJsonArray()) {
			for (JsonElement child : ele.getAsJsonArray()) {
				readInto(out, child);
			}
		} else {
			readInto(out, ele);
		}
		return out;
	}

	private static void readInto(FluidIngredient out, JsonElement ele) {
		if (!ele.isJsonObject()) throw new IllegalArgumentException("Expected object, got "+ele);
		JsonObject obj = ele.getAsJsonObject();
		if (obj.has("fluid")) {
			out.exacts.add(Registry.FLUID.get(Identifier.tryParse(obj.get("fluid").getAsString())));
		} else if (obj.has("tag")) {
			out.tags.add(AccessorFluidTags.getRequiredTags().getGroup().getTag(Identifier.tryParse(obj.get("tag").getAsString())));
		} else {
			throw new IllegalArgumentException("Don't know how to parse "+ele+" without a fluid or tag value");
		}
	}
	
}
