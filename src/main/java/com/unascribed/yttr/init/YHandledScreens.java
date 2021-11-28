package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.client.screen.handled.CanFillerScreen;
import com.unascribed.yttr.client.screen.handled.CentrifugeScreen;
import com.unascribed.yttr.client.screen.handled.DSUScreen;
import com.unascribed.yttr.client.screen.handled.MagtankScreen;
import com.unascribed.yttr.client.screen.handled.SuitStationScreen;
import com.unascribed.yttr.client.screen.handled.VoidFilterScreen;
import com.unascribed.yttr.inventory.CanFillerScreenHandler;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;
import com.unascribed.yttr.inventory.DSUScreenHandler;
import com.unascribed.yttr.inventory.MagtankScreenHandler;
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
	@Screen(DSUScreen.class)
	public static final ScreenHandlerType<DSUScreenHandler> DSU = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "dsu"), DSUScreenHandler::new);
	@Screen(MagtankScreen.class)
	public static final ScreenHandlerType<MagtankScreenHandler> MAGTANK = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "magtank"), MagtankScreenHandler::new);
	@Screen(CanFillerScreen.class)
	public static final ScreenHandlerType<CanFillerScreenHandler> CAN_FILLER = ScreenHandlerRegistry.registerSimple(new Identifier("yttr", "can_filler"), CanFillerScreenHandler::new);
	
	public static void init() {}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Screen {
		Class<? extends HandledScreen<?>> value();
	}
	
}
