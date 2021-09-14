package com.unascribed.yttr.compat.rei;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class CentrifugingCategory extends DrawableHelper implements RecipeCategory<CentrifugingEntry> {

	public static final Identifier ID = new Identifier("yttr", "centrifuging");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.centrifuging");
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return EntryStack.create(YBlocks.CENTRIFUGE);
	}

	@Override
	public int getDisplayHeight() {
		return 108;
	}
	
	@Override
	public int getDisplayWidth(CentrifugingEntry display) {
		return 150;
	}

	@Override
	public @NotNull List<Widget> setupDisplay(CentrifugingEntry recipe, Rectangle bounds) {
		List<Widget> widgets = Lists.newArrayList();
		int cX = bounds.getCenterX();
		int cY = bounds.getCenterY();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/centrifuge.png"), cX-47, cY-47, 41, 8, 94, 94));
		widgets.add(Widgets.createSlot(new Point(cX-8, cY-7)).entries(recipe.getInputEntries().get(0)).disableBackground().markInput());
		if (recipe.getResultingEntries().size() >= 1) {
			widgets.add(Widgets.createSlot(new Point(cX-4, cY-42)).entries(recipe.getResultingEntries().get(0)).disableBackground().markOutput());
		}
		if (recipe.getResultingEntries().size() >= 2) {
			widgets.add(Widgets.createSlot(new Point(cX+26, cY-3)).entries(recipe.getResultingEntries().get(1)).disableBackground().markOutput());
		}
		if (recipe.getResultingEntries().size() >= 3) {
			widgets.add(Widgets.createSlot(new Point(cX-12, cY+27)).entries(recipe.getResultingEntries().get(2)).disableBackground().markOutput());
		}
		if (recipe.getResultingEntries().size() >= 4) {
			widgets.add(Widgets.createSlot(new Point(cX-42, cY-11)).entries(recipe.getResultingEntries().get(3)).disableBackground().markOutput());
		}
		widgets.add(Widgets.createBurningFire(new Point(bounds.getMinX()+6, bounds.getMaxY()-20)).animationDurationTicks(100));
		return widgets;
	}

}
