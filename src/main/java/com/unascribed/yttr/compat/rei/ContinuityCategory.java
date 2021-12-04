package com.unascribed.yttr.compat.rei;

import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import com.unascribed.yttr.init.YItems;

import com.google.common.collect.Lists;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class ContinuityCategory implements RecipeCategory<ContinuityEntry> {
	
	public static final Identifier ID = new Identifier("yttr", "continuity");
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	
    @Override
    public @NotNull Identifier getIdentifier() {
        return ID;
    }
    
    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate("category.yttr.continuity");
    }
    
    @Override
    public @NotNull EntryStack getLogo() {
        return EntryStack.create(YItems.DROP_OF_CONTINUITY);
    }
    
    @Override
    public @NotNull RecipeEntry getSimpleRenderer(ContinuityEntry recipe) {
        String name = getCategoryName();
        return new RecipeEntry() {
            @Override
            public int getHeight() {
                return 10 + MinecraftClient.getInstance().textRenderer.fontHeight;
            }
            
            @Override
            public void render(MatrixStack matrices, Rectangle rectangle, int mouseX, int mouseY, float delta) {
                MinecraftClient.getInstance().textRenderer.draw(matrices, name, rectangle.x + 5, rectangle.y + 6, -1);
            }
        };
    }
    
    @Override
    public @NotNull List<Widget> setupDisplay(ContinuityEntry display, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createSlot(new Point(bounds.getCenterX() - 8, bounds.y + 3)).entry(getLogo()));
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX() + 14, bounds.y + 7), new TranslatableText("category.yttr.continuity.chance", DECIMAL_FORMAT.format(100D/display.getEntries().size())))
				.color(0xFF404040, 0xFFBBBBBB).noShadow().leftAligned());
        Rectangle rectangle = new Rectangle(bounds.getCenterX() - (bounds.width / 2) - 1, bounds.y + 23, bounds.width + 2, bounds.height - 28);
        widgets.add(Widgets.createSlotBase(rectangle));
        widgets.add(new ScrollableSlotsWidget(rectangle, CollectionUtils.map(display.getEntries(), t -> Widgets.createSlot(new Point(0, 0)).disableBackground().entry(t))));
        return widgets;
    }
    
    @Override
    public int getDisplayHeight() {
        return 140;
    }
    
    @Override
    public int getFixedRecipesPerPage() {
        return 1;
    }
    
    // copied from DefaultBeaconPaymentCategory
    private static class ScrollableSlotsWidget extends WidgetWithBounds {
        private Rectangle bounds;
        private List<Slot> widgets;
        private final ScrollingContainer scrolling = new ScrollingContainer() {
            @Override
            public Rectangle getBounds() {
                return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
            }
            
            @Override
            public int getMaxScrollHeight() {
                return MathHelper.ceil(widgets.size() / 8f) * 18;
            }
        };
        
        public ScrollableSlotsWidget(Rectangle bounds, List<Slot> widgets) {
            this.bounds = Objects.requireNonNull(bounds);
            this.widgets = Lists.newArrayList(widgets);
        }
        
        @Override
        public boolean mouseScrolled(double double_1, double double_2, double double_3) {
            if (containsMouse(double_1, double_2)) {
                scrolling.offset(ClothConfigInitializer.getScrollStep() * -double_3, true);
                return true;
            }
            return false;
        }
        
        @NotNull
        @Override
        public Rectangle getBounds() {
            return bounds;
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (scrolling.updateDraggingState(mouseX, mouseY, button))
                return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }
        
        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (scrolling.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            scrolling.updatePosition(delta);
            Rectangle innerBounds = scrolling.getScissorBounds();
            ScissorsHandler.INSTANCE.scissor(innerBounds);
            for (int y = 0; y < MathHelper.ceil(widgets.size() / 8f); y++) {
                for (int x = 0; x < 8; x++) {
                    int index = y * 8 + x;
                    if (widgets.size() <= index)
                        break;
                    // Yttr: Don't render slots outside of the bounds of the scrolling area
                    int yp = (int) (bounds.y + 1 + y * 18 - scrolling.scrollAmount);
                    if (yp < bounds.y-16 || yp > bounds.getMaxY()-1) continue;
                    Slot widget = widgets.get(index);
                    widget.getBounds().setLocation(bounds.x + 1 + x * 18, yp);
                    widget.render(matrices, mouseX, mouseY, delta);
                }
            }
            ScissorsHandler.INSTANCE.removeLastScissor();
            ScissorsHandler.INSTANCE.scissor(scrolling.getBounds());
            scrolling.renderScrollBar(0xff000000, 1, REIHelper.getInstance().isDarkThemeEnabled() ? 0.8f : 1f);
            ScissorsHandler.INSTANCE.removeLastScissor();
        }
        
        @Override
        public List<? extends Element> children() {
            return widgets;
        }
    }
}
