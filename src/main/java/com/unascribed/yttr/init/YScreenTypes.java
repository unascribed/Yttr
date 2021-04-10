package com.unascribed.yttr.init;

import com.unascribed.yttr.annotate.Screen;
import com.unascribed.yttr.client.CentrifugeScreen;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class YScreenTypes {

	@Screen(CentrifugeScreen.class)
	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "centrifuge"), CentrifugeScreenHandler::new);
	
	public static void init() {}
	
}
