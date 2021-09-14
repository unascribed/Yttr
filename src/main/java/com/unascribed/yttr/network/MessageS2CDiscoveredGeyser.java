package com.unascribed.yttr.network;

import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.world.Geyser;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;

public class MessageS2CDiscoveredGeyser extends S2CMessage {
	
	public Geyser geyser;
	
	public MessageS2CDiscoveredGeyser(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDiscoveredGeyser(Geyser geyser) {
		super(YNetwork.CONTEXT);
		this.geyser = geyser;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).addGeyser(geyser);
		} else {
			String name = geyser.name;
			mc.getToastManager().add((matrices, manager, startTime) -> {
				manager.getGame().getTextureManager().bindTexture(Toast.TEXTURE);
				manager.drawTexture(matrices, 0, 0, 0, 0, 160, 32);
				manager.getGame().getTextureManager().bindTexture(SuitRenderer.SUIT_TEX);
				DrawableHelper.drawTexture(matrices, 4, 4, 23, 18, 12, 12, SuitRenderer.SUIT_TEX_WIDTH, SuitRenderer.SUIT_TEX_HEIGHT);
				manager.getGame().textRenderer.draw(matrices, "§l"+I18n.translate("yttr.geyser_discovered"), 30, 7, -1);
				manager.getGame().textRenderer.draw(matrices, name, 30, 18, -1);
				return startTime >= 5000 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
			});
		}
	}

}
