package com.unascribed.yttr.command;

import java.util.Collections;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

public class DebugTeleportCommand {

	public static LiteralArgumentBuilder<ServerCommandSource> create() {
		return CommandManager.literal("yttr:dtp")
				.requires((scs) -> scs.hasPermissionLevel(4) && scs.getWorld().isDebugWorld())
				.then(CommandManager.argument("state", BlockStateArgumentType.blockState())
					.executes((ctx) -> {
						BlockState state = BlockStateArgumentType.getBlockState(ctx, "state").getBlockState();
						int idx = DebugChunkGenerator.BLOCK_STATES.indexOf(state);
						if (idx >= 0) {
							int x = idx/DebugChunkGenerator.X_SIDE_LENGTH;
							int z = idx%DebugChunkGenerator.X_SIDE_LENGTH;
							x *= 2;
							z *= 2;
							x += 1;
							z += 1;
							ServerPlayerEntity p = ctx.getSource().getPlayer();
							p.networkHandler.requestTeleport(x+0.5, 75, z+0.5, 0, 90, Collections.emptySet());
							String stateStr = state.toString().replaceFirst("^\\Q"+state.getBlock().toString()+"\\E", Registry.BLOCK.getId(state.getBlock()).toString());
							ctx.getSource().sendFeedback(new TranslatableText("commands.teleport.success.entity.single", p.getDisplayName(), stateStr), true);
						}
						return 0;
					})
				);
	}
	
}
