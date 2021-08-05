package com.unascribed.yttr.mixin.substitute;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.unascribed.yttr.Substitutes;

import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.tag.SetTag;

@Mixin(SetTag.class)
public class MixinSetTag {

	@SuppressWarnings("unchecked")
	@ModifyVariable(at=@At("HEAD"), method="<init>", argsOnly=true, ordinal=0)
	private static Set<?> replaceSet(Set<?> values, Set<?> values2, Class<?> type) {
		if (type.isAssignableFrom(Item.class)) {
			Set<Item> ins = Sets.intersection((Set<Item>)values, Substitutes.allPrimes());
			if (!ins.isEmpty()) {
				Set<Item> newSet = Sets.newLinkedHashSet();
				newSet.addAll((Set<Item>)values);
				for (Item i : ins) {
					newSet.add(Substitutes.getSubstitute(i));
				}
				return newSet;
			}
		}
		return values;
	}
	
}
