package com.unascribed.yttr.util;

import net.minecraft.entity.attribute.EntityAttribute;

public class FakeEntityAttribute extends EntityAttribute {

	public FakeEntityAttribute(String translationKey) {
		super(translationKey, 0);
	}

}
