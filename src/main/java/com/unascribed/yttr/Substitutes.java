package com.unascribed.yttr;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Substitutes {

	private static final Gson gson = new Gson();
	private static final BiMap<Item, Item> MAP = HashBiMap.create();
	
	public static void reload(ResourceManager mgr) {
		MAP.clear();
		for (String ns : mgr.getAllNamespaces()) {
			Identifier id = new Identifier(ns, "yttr_substitutes.json");
			if (mgr.containsResource(id)) {
				try {
					Resource r = mgr.getResource(id);
					try (InputStreamReader isr = new InputStreamReader(r.getInputStream(), Charsets.UTF_8)) {
						JsonObject obj = gson.fromJson(isr, JsonObject.class);
						for (Map.Entry<String, JsonElement> en : obj.entrySet()) {
							String k = en.getKey();
							boolean optionalK = false;
							if (k.endsWith("?")) {
								k = k.substring(0, k.length()-1);
								optionalK = true;
							}
							Identifier kId = new Identifier(k);
							Optional<Item> kI = Registry.ITEM.getOrEmpty(kId);
							if (kI.isPresent()) {
								String v = en.getValue().getAsString();
								boolean optionalV = false;
								if (v.endsWith("?")) {
									v = v.substring(0, v.length()-1);
									optionalV = true;
								}
								Identifier vId = new Identifier(v);
								Optional<Item> vI = Registry.ITEM.getOrEmpty(vId);
								if (vI.isPresent()) {
									if (MAP.containsKey(kI.get())) {
										if (!optionalK) YLog.warn("While loading "+id+" substitute "+kId+" to prime "+vId+", a mapping already exists for this substitute to prime "+Registry.ITEM.getId(MAP.get(kI.get()))+" - ignoring this mapping. Add a ? to make it optional and silence this warning.");
									} else if (MAP.containsValue(vI.get())) {
										if (!optionalV) YLog.warn("While loading "+id+" substitute "+kId+" to prime "+vId+", a mapping already exists for this prime to substitute "+Registry.ITEM.getId(MAP.inverse().get(vI.get()))+" - ignoring this mapping. Add a ? to make it optional and silence this warning.");
									} else {
										MAP.put(kI.get(), vI.get());
									}
								} else if (!optionalV) {
									YLog.warn("While loading "+id+" substitute "+kId+", could not find item with ID "+vId+" for prime (add a ? to make it optional and silence this warning)");
								}
							} else if (!optionalK) {
								YLog.warn("While loading "+id+", could not find item with ID "+kId+" for substitute (add a ? to make it optional and silence this warning)");
							}
						}
					}
				} catch (Throwable e) {
					YLog.error("Failed to load "+id, e);
				}
			}
		}
		YLog.info("Loaded "+MAP.size()+" substitution"+(MAP.size() == 1 ? "" : "s"));
	}
	
	public static Set<Item> allPrimes() {
		return MAP.inverse().keySet();
	}
	
	public static Set<Item> allSubstitutes() {
		return MAP.keySet();
	}
	
	public static @Nullable Item getPrime(Item substitute) {
		return MAP.get(substitute);
	}
	
	public static @Nullable Item getSubstitute(Item prime) {
		return MAP.inverse().get(prime);
	}
	
	public static ItemStack sub(ItemStack stack) {
		return copyWithAltItem(stack, getSubstitute(stack.getItem()));
	}

	public static ItemStack prime(ItemStack stack) {
		return copyWithAltItem(stack, getPrime(stack.getItem()));
	}
	
	private static ItemStack copyWithAltItem(ItemStack stack, Item item) {
		if (item == null) return stack.copy();
		ItemStack copy = new ItemStack(item);
		copy.setCount(stack.getCount());
		copy.setTag(stack.getTag());
		return copy;
	}
	
}
