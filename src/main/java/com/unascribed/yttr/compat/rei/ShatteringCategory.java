package com.unascribed.yttr.compat.rei;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.unascribed.yttr.init.YEnchantments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.api.EntryStack.Settings;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ShatteringCategory implements RecipeCategory<ShatteringEntry> {

	public static final Identifier ID = new Identifier("yttr", "shattering");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull EntryStack getLogo() {
		ItemStack is = new ItemStack(Items.DIAMOND_PICKAXE);
		EnchantmentHelper.set(ImmutableMap.of(YEnchantments.SHATTERING_CURSE, 1), is);
		return EntryStack.create(is).setting(Settings.CHECK_TAGS, () -> true);
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.shattering");
	}

	@Override
	public @NotNull List<Widget> setupDisplay(ShatteringEntry display, Rectangle bounds) {
		Point startPoint = new Point(bounds.getCenterX() - 40, bounds.getCenterY() - 9);
		List<Widget> widgets = Lists.newArrayList();
		widgets.add(Widgets.createRecipeBase(bounds));
		if (display.exclusive) {
			widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/shattering.png"), new Rectangle(startPoint.x + 24, startPoint.y + 1, 24, 17),
					0, REIHelper.getInstance().isDarkThemeEnabled() ? 17 : 0, 24, 36));
		} else {
			widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 1)));
		}
		widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 60, startPoint.y + 1)));
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entries(display.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createSlot(new Point(startPoint.x + 60, startPoint.y + 1)).entries(display.getResultingEntries().get(0)).disableBackground().markOutput());
		return widgets;
	}
	
	@Override
	public int getDisplayHeight() {
		return 40;
	}

}
