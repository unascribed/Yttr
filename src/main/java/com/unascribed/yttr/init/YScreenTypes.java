package com.unascribed.yttr.init;

import com.unascribed.yttr.client.CentrifugeScreen;
import com.unascribed.yttr.client.SuitStationScreen;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;
import com.unascribed.yttr.inventory.SuitStationScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class YScreenTypes {

	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "centrifuge"), CentrifugeScreenHandler::new);
	public static final ScreenHandlerType<SuitStationScreenHandler> SUIT_STATION = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "suit_station"), SuitStationScreenHandler::new);
	
	public static void init() {}
	
}
