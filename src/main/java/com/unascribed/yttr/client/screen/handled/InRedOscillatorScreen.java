package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.platform.GlStateManager;
import com.unascribed.yttr.content.block.inred.InRedOscillatorBlockEntity;
import com.unascribed.yttr.inventory.InRedOscillatorScreenHandler;
import com.unascribed.yttr.network.MessageC2SOscillatorShift;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class InRedOscillatorScreen extends HandledScreen<InRedOscillatorScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("yttr:textures/gui/inred_oscillator.png");

	private BlockPos pos;
	private BlockState state;
	private InRedOscillatorBlockEntity be;

	public InRedOscillatorScreen(InRedOscillatorScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.pos = handler.getPos();
		this.state = handler.getState();
		this.be = handler.getOscillator();
		be.readNbt(state, inventory.player.getEntityWorld().getBlockEntity(pos).writeNbt(new NbtCompound()));
		this.width = 138;
		this.height = 74;
	}

	@Override
	protected void init() {
		super.init();
		int topPadded = ((this.backgroundHeight - this.height) / 2 + 5);
		int leftPadded = ((this.backgroundWidth - this.width) / 2) + 5;
		this.addButton(new OscillatorButtonWidget(leftPadded+72, topPadded+32, "tick_up", button -> new MessageC2SOscillatorShift(be, 1).sendToServer()));
		this.addButton(new OscillatorButtonWidget(leftPadded+40, topPadded+32, "tick_down", button -> new MessageC2SOscillatorShift(be, -1).sendToServer()));
		this.addButton(new OscillatorButtonWidget(leftPadded+104, topPadded+32, "second_up", button -> new MessageC2SOscillatorShift(be, 10).sendToServer()));
		this.addButton(new OscillatorButtonWidget(leftPadded+8, topPadded+32, "second_down", button -> new MessageC2SOscillatorShift(be, -10).sendToServer()));
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int guiX = (this.backgroundWidth - this.width) / 2;
		int guiY = (this.backgroundHeight - this.height) / 2;
		this.drawTexture(matrices, guiX, guiY, 0, 0, this.width, this.height);
		String text = String.format(""+ Formatting.DARK_GRAY+"Delay: %s second%s", formatSeconds(be.maxRefreshTicks), (be.maxRefreshTicks != 10)? "s": "");
		textRenderer.draw(matrices, text, (width / 2f) - (textRenderer.getWidth(text) / 2f), (height / 2f) - (textRenderer.fontHeight * 3f), 1);
	}

	private String formatSeconds(int ticks) {
		if (ticks % 10 == 0) {
			int ret = ticks/10;
			return (""+ret);
		} else {
			double ret = (double)ticks/10;
			return (""+ret);
		}
	}

	public static class OscillatorButtonWidget extends ButtonWidget {
		public Identifier tex;
		private final int u = 0;
		private final int v = 0;
		private final int hoverVOffset = 16;

		public OscillatorButtonWidget(int x, int y, String name, PressAction onPress) {
			super(x, y, 16, 16, new LiteralText(""), onPress);
			tex = new Identifier("yttr:textures/gui/inred_button_"+name+".png");
			this.visible = true;
		}

		//TODO: button narration! important!!
//		@Override
//		protected MutableText getNarrationMessage() {
//			return new TranslatableText("tip.yttr.inred.oscillator.narrator");
//		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if (this.visible) {
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				MinecraftClient client = MinecraftClient.getInstance();
				client.getTextureManager().bindTexture(this.tex);
				GlStateManager.disableDepthTest();
				int hotV = this.v;
				if (this.hovered) {
					hotV += this.hoverVOffset;
				}

				this.drawTexture(matrices, this.x, this.y, this.u, hotV, this.width, this.height);
				GlStateManager.enableDepthTest();
			}
		}
	}
}
