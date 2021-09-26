package com.unascribed.yttr.compat.rei;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SoakingCategory implements RecipeCategory<SoakingEntry> {

	public static final Identifier ID = new Identifier("yttr", "soaking");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.soaking");
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return EntryStack.create(Items.BUCKET);
	}
	
	@Override
	public int getDisplayHeight() {
		return 62;
	}

	@Override
	public @NotNull List<Widget> setupDisplay(SoakingEntry recipe, Rectangle bounds) {
		int cX = bounds.getCenterX()+32;
		int cY = bounds.getCenterY()-4;
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		if (recipe.consumesCatalyst()) {
			widgets.add(Widgets.createLabel(new Point(bounds.x+6, bounds.getMaxY() - 14), new TranslatableText("category.yttr.soaking.consumes_catalyst"))
					.color(0xFF404040, 0xFFBBBBBB).noShadow().leftAligned());
		}
		widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/curved_arrow.png"), cX-15, cY-20, 0, 0, 16, 16, 16, 16));
		widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/curved_arrow_down.png"), cX-32, cY-20, 0, 0, 16, 16, 16, 16));
		widgets.add(Widgets.createSlot(new Point(cX+5, cY-20)).entries(recipe.getOutput()).markOutput());
		int oX = -recipe.getInput().size()*18;
		for (List<EntryStack> in : recipe.getInput()) {
			widgets.add(Widgets.createSlot(new Point(cX-34+oX, cY-20)).entries(in).markInput());
			oX += 18;
		}
		widgets.add(Widgets.createSlotBase(new Rectangle(cX-33, cY-1, 34, 18)));
		widgets.add(Widgets.createSlot(new Point(cX-16, cY)).entries(recipe.getCatalyst()).markInput().disableBackground());
		widgets.add(Widgets.createSlot(new Point(cX-32, cY)).entries(recipe.getCatalyst()).markInput().disableBackground());
		return widgets;
	}
	
}
