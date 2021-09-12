package com.unascribed.yttr.init;

import com.unascribed.yttr.inventory.CentrifugeScreenHandler;
import com.unascribed.yttr.inventory.HighStackGenericContainerScreenHandler;
import com.unascribed.yttr.inventory.SuitStationScreenHandler;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class YScreenTypes {

	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "centrifuge"), CentrifugeScreenHandler::new);
	public static final ScreenHandlerType<SuitStationScreenHandler> SUIT_STATION = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "suit_station"), SuitStationScreenHandler::new);
	public static final ScreenHandlerType<VoidFilterScreenHandler> VOID_FILTER = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "void_filter"), VoidFilterScreenHandler::new);

	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X1 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x1"), HighStackGenericContainerScreenHandler::createGeneric9x1);
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X2 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x2"), HighStackGenericContainerScreenHandler::createGeneric9x2);
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X3 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x3"), HighStackGenericContainerScreenHandler::createGeneric9x3);
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X4 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x4"), HighStackGenericContainerScreenHandler::createGeneric9x4);
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X5 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x5"), HighStackGenericContainerScreenHandler::createGeneric9x5);
	public static final ScreenHandlerType<HighStackGenericContainerScreenHandler> HIGH_STACK_GENERIC_9X6 = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "high_stack_generic_9x6"), HighStackGenericContainerScreenHandler::createGeneric9x6);
	
	public static void init() {}
	
}
