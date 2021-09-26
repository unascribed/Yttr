package com.unascribed.yttr.compat.rei;

import org.jetbrains.annotations.Nullable;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.gui.widget.EntryWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class RuinedEntryWidget extends EntryWidget {

	public RuinedEntryWidget(Point point) {
		super(point);
	}
	
	@Override
	protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {
	}
	
	@Override
	public @Nullable Tooltip getCurrentTooltip(Point point) {
		if (getCurrentEntry().isEmpty()) {
			return Tooltip.create(new TranslatableText("container.enchant.clue").formatted(Formatting.ITALIC));
		} else {
			return Tooltip.create(new TranslatableText("container.enchant.clue", new TranslatableText(getCurrentEntry().getItem().getTranslationKey()+".alt")).formatted(Formatting.ITALIC));
		}
	}

}
