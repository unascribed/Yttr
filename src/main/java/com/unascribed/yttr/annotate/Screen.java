package com.unascribed.yttr.annotate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Screen {

	Class<? extends HandledScreen<?>> value();
	
}
