package com.unascribed.yttr.compat.rei;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.init.YBlocks;
import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class RuinedCategory implements RecipeCategory<RuinedEntry> {

	public static final Identifier ID = new Identifier("yttr", "ruined");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.ruined");
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return EntryStack.create(YBlocks.WASTELAND_DIRT);
	}
	
	@Override
	public int getDisplayHeight() {
		return 60;
	}
	
	@Override
	public @NotNull List<Widget> setupDisplay(RuinedEntry recipe, Rectangle bounds) {
		int x = bounds.getCenterX();
		int y = bounds.y;
		double aspect = 696/324D;
		int h = 54;
		int w = (int)(h*aspect);
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
			MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier(recipe.getId().getNamespace(), "textures/gui/ruined_recipe/"+recipe.getId().getPath()+".png"));
			matrices.push();
			matrices.translate(x-(w/2f), y, 0);
			DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, w, h, w, h);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(SrcFactor.ZERO, DstFactor.SRC_COLOR); // multiply
			MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("yttr", "textures/gui/ruined_recipe/overlay.png"));
			DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, w, h, w, h);
			
			double wFactor = 872/696D;
			double hFactor = 500/324D;
			
			matrices.push();
			matrices.translate(w/2f, h/2f, 0);
			matrices.scale((float)wFactor, (float)hFactor, 1);
			matrices.translate(-w/2f, -h/2f, 0);
			RenderSystem.defaultBlendFunc();
			MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("yttr", "textures/gui/ruined_recipe/border.png"));
			RenderSystem.disableAlphaTest();
			DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, w, h, w, h);
			matrices.pop();
			matrices.pop();
		}));
		int bX = x-(w/2);
		int bY = y;
		widgets.add(new RuinedEntryWidget(new Point(bX+95, bY+19)).entry(recipe.getResult()).disableBackground());
		for (int i = 0; i < 9; i++) {
			RuinedEntryWidget widget = new RuinedEntryWidget(new Point(bX+1+((i%3)*18), bY+1+((i/3)*18)));
			widget.disableBackground();
			if (recipe.getEmptySlots().contains(i)) {
				widget.disableTooltips();
			}
			widgets.add(widget);
		}
		return widgets;
	}
	
}
