package com.unascribed.yttr.init;

import com.unascribed.yttr.command.DebugTeleportCommand;
import com.unascribed.yttr.command.VoidUndoCommand;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class YCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedi) -> {
			dispatcher.register(VoidUndoCommand.create());
			dispatcher.register(DebugTeleportCommand.create());
		});
	}
	
}
