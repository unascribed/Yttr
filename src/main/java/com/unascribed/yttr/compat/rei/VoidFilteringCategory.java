package com.unascribed.yttr.compat.rei;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YItems;

import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class VoidFilteringCategory implements RecipeCategory<VoidFilteringEntry> {

	public static final Identifier ID = new Identifier("yttr", "void_filtering");
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.void_filtering");
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return EntryStack.create(YItems.VOID_FILTER);
	}
	
	@Override
	public int getDisplayHeight() {
		return 28;
	}

	@Override
	public @NotNull List<Widget> setupDisplay(VoidFilteringEntry recipe, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 17);
		String chancePct = DECIMAL_FORMAT.format(recipe.getChance());
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createLabel(new Point(bounds.x + 26, bounds.getMaxY() - 15), new TranslatableText("category.yttr.void_filtering.chance", chancePct))
				.color(0xFF404040, 0xFFBBBBBB).noShadow().leftAligned());
		widgets.add(Widgets.createSlot(new Point(bounds.x + 6, startPoint.y + 10)).entries(recipe.getResultingEntries().get(0)).markOutput());
		return widgets;
	}

	@Override
	public @NotNull RecipeEntry getSimpleRenderer(VoidFilteringEntry recipe) {
		Slot slot = Widgets.createSlot(new Point(0, 0)).entries(recipe.getResultingEntries().get(0)).disableBackground().disableHighlight();
		String chancePct = DECIMAL_FORMAT.format(recipe.getChance());
		return new RecipeEntry() {
			private TranslatableText text = new TranslatableText("category.yttr.void_filtering.chance.short", chancePct);

			@Override
			public int getHeight() {
				return 22;
			}

			@Nullable
			@Override
			public Tooltip getTooltip(Point point) {
				if (slot.containsMouse(point))
					return slot.getCurrentTooltip(point);
				return null;
			}

			@Override
			public void render(MatrixStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
				slot.setZ(getZ() + 50);
				slot.getBounds().setLocation(bounds.x + 4, bounds.y + 2);
				slot.render(matrices, mouseX, mouseY, delta);
				MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text.asOrderedText(), bounds.x + 25, bounds.y + 8, -1);
			}
		};
	}
	
}
