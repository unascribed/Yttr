package com.unascribed.yttr.client.screen.handled;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.inventory.RafterScreenHandler;
import com.unascribed.yttr.inventory.RafterScreenHandler.FloatingSlot;
import com.unascribed.yttr.mixin.accessor.client.AccessorHandledScreen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class RafterScreen extends HandledScreen<RafterScreenHandler> implements RecipeBookProvider {
	private static final Identifier TEXTURE = new Identifier("yttr", "textures/gui/rafter.png");
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean narrow;
	private int ticks;

	public RafterScreen(RafterScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 201;
		this.backgroundHeight = 194;
		this.titleY = 44;
		this.titleX = 121;
		this.playerInventoryTitleX = 36;
		this.playerInventoryTitleY = 111;
	}

	@Override
	protected void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width-40, this.height, this.client, this.narrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
		this.children.add(this.recipeBook);
		this.setInitialFocus(this.recipeBook);
		this.addButton(new TexturedButtonWidget(this.x + 113, this.y + 97, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (buttonWidget) -> {
			this.recipeBook.reset(this.narrow);
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
			((TexturedButtonWidget)buttonWidget).setPos(this.x + 113, this.y + 97);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		ticks++;
		this.recipeBook.update();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(matrices, delta, mouseX, mouseY);
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(matrices, mouseX, mouseY, delta);
			super.render(matrices, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(matrices, this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int x = this.x;
		int y = (this.height - this.backgroundHeight) / 2;
		drawTexture(matrices, x+36, y+36, 36, 36, 165, 168);
		drawTexture(matrices, x+0, y+108, 0, 108, 36, 96);
		drawTexture(matrices, x+108, y+0, 108, 0, 93, 36);
		Random r = new Random(hashCode());
		for (int sx = 0; sx < 6; sx++) {
			for (int sy = 0; sy < 6; sy++) {
				int i = (sx+(sy*6));
				Slot s = handler.getSlot(1+i);
				if (s instanceof FloatingSlot) {
					float t = (ticks+delta)/200;
					int px = sx*18;
					int py = sy*18;
					float ox = (sq((4-sx)+1)*-0.5f)+(MathHelper.sin(t+(r.nextFloat()*4))*2);
					float oy = (sq((4-sy)+1)*-0.5f)+(MathHelper.cos(t+(r.nextFloat()*4))*2);
					float ang = (float) (r.nextGaussian()*10+MathHelper.sin(t+(r.nextFloat()*60))*20);
					if (t < 0.1) {
						ox = 0;
						oy = 0;
						ang = 0;
					} else if (t < 2.1) {
						ox *= ((t-0.1)/2);
						oy *= ((t-0.1)/2);
						ang *= ((t-0.1)/2);
					}
					FloatingSlot fs = (FloatingSlot)s;
					fs.floatingX = px+ox;
					fs.floatingY = py+oy;
					fs.ang = ang;
					matrices.push();
						matrices.translate(x+px+ox, y+py+oy, 0);
						matrices.translate(9, 9, 0);
						matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(ang));
						drawTexture(matrices, -9, -9, px, py, 18, 18);
					matrices.pop();
				}
			}
		}
	}

	private int sq(int i) {
		return i * i;
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		for (int i = 0; i < this.handler.slots.size(); ++i) {
			Slot slot = this.handler.slots.get(i);
			if (slot instanceof FloatingSlot) {
				FloatingSlot fs = (FloatingSlot)slot;
				RenderSystem.pushMatrix();
					RenderSystem.translatef(fs.floatingX, fs.floatingY, 0);
					RenderSystem.translatef(9, 9, 0);
					RenderSystem.rotatef(fs.ang, 0, 0, 1);
					RenderSystem.translatef(-8, -8, 0);
					((AccessorHandledScreen)this).yttr$drawSlot(matrices, slot);
	
					Path2D.Float path = getPathFor(fs);
					if (path.contains(mouseX-this.x, mouseY-this.y)) {
						this.focusedSlot = slot;
						RenderSystem.disableDepthTest();
						RenderSystem.colorMask(true, true, true, false);
						fillGradient(matrices, 0, 0, 16, 16, -2130706433, -2130706433);
						RenderSystem.colorMask(true, true, true, true);
						RenderSystem.enableDepthTest();
					}
				RenderSystem.popMatrix();
			}
		}
		super.drawForeground(matrices, mouseX, mouseY);
	}
	
	private Path2D.Float getPathFor(FloatingSlot fs) {
		Path2D.Float path = new Path2D.Float();
		Rectangle2D.Float rect = new Rectangle2D.Float(-8, -8, 16, 16);
		path.append(rect, true);
		path.transform(AffineTransform.getRotateInstance(Math.toRadians(fs.ang)));
		path.transform(AffineTransform.getTranslateInstance(8, 8));
		path.transform(AffineTransform.getTranslateInstance(fs.floatingX, fs.floatingY));
		return path;
	}
	
	public Slot getAltSlotAt(double x, double y) {
		for (int i = 0; i < this.handler.slots.size(); ++i) {
			Slot slot = this.handler.slots.get(i);
			if (slot instanceof FloatingSlot) {
				FloatingSlot fs = (FloatingSlot)slot;
				Path2D.Float path = getPathFor(fs);
				if (path.contains(x-this.x, y-this.y)) {
					return slot;
				}
			}
		}
		return null;
	}

	@Override
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocused(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < left || mouseY < top || mouseX >= left + this.backgroundWidth || mouseY >= top + this.backgroundHeight;
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public void removed() {
		this.recipeBook.close();
		super.removed();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
