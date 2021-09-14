package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.client.screen.handled.CentrifugeScreen;
import com.unascribed.yttr.client.screen.handled.HighStackGenericContainerScreen;
import com.unascribed.yttr.client.screen.handled.SuitStationScreen;
import com.unascribed.yttr.client.screen.handled.VoidFilterScreen;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;
import com.unascribed.yttr.inventory.HighStackGenericContainerScreenHandler;
import com.unascribed.yttr.inventory.SuitStationScreenHandler;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class YHandledScreens {

	@Screen(CentrifugeScreen.class)
	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "centrifuge"), CentrifugeScreenHandler::new);
	@Screen(SuitStationScreen.class)
	public static final ScreenHandlerType<SuitStationScreenHandler> SUIT_STATION = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "suit_station"), SuitStationScreenHandler::new);
	@Screen(VoidFilterScreen.class)
	public static final ScreenHandlerType<VoidFilterScreenHandler> VOID_FILTER = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "void_filter"), VoidFilterScreenHandler::new);

	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X1 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x1"), HighStackGenericContainerScreenHandler::createGeneric9x1);
	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X2 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x2"), HighStackGenericContainerScreenHandler::createGeneric9x2);
	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X3 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x3"), HighStackGenericContainerScreenHandler::createGeneric9x3);
	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X4 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x4"), HighStackGenericContainerScreenHandler::createGeneric9x4);
	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X5 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x5"), HighStackGenericContainerScreenHandler::createGeneric9x5);
	@Screen(HighStackGenericContainerScreen.class)
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X6 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x6"), HighStackGenericContainerScreenHandler::createGeneric9x6);
	
	public static void init() {}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Screen {
		Class<? extends HandledScreen<?>> value();
	}
	
}
