package com.unascribed.yttr.annotate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Renderer {

	Class<? extends BlockEntityRenderer<?>> value();
	
}
