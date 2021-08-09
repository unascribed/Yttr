package com.unascribed.yttr.compat;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.mojang.blaze3d.systems.RenderSystem;

import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class PistonSmashingCategory implements RecipeCategory<PistonSmashingEntry> {

	public static final Identifier ID = new Identifier("yttr", "piston_smashing");

	@Override
	public @NotNull Identifier getIdentifier() {
		return ID;
	}

	@Override
	public @NotNull String getCategoryName() {
		return I18n.translate("category.yttr.piston_smashing");
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return EntryStack.create(Blocks.PISTON);
	}

	@Override
	public int getDisplayHeight() {
		return 56;
	}

	@Override
	public @NotNull List<Widget> setupDisplay(PistonSmashingEntry recipe, Rectangle bounds) {
		List<Widget> widgets = Lists.newArrayList();
		int cX = bounds.getCenterX();
		int cY = bounds.getCenterY();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createSlot(new Point(cX + 15, cY - 22)).entries(recipe.getResultingEntries().get(0)).markOutput());
		widgets.add(Widgets.createSlot(new Point(cX - 8, cY)).entries(recipe.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/curved_arrow.png"), cX-5, cY-20, 0, 0, 16, 16, 16, 16));
		ItemStack piston = new ItemStack(Items.PISTON);
		List<ItemStack> catalysts = Lists.transform(recipe.getRequiredEntries().get(0), EntryStack::getItemStack);
		widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
			VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			int light = LightmapTextureManager.pack(15, 15);
			int overlay = OverlayTexture.DEFAULT_UV;
			RenderSystem.color4f(1, 1, 1, 1);
			matrices.push();
			matrices.translate(cX-1, cY+8, 50);
			matrices.scale(16, -16, 16);
			matrices.translate(-2, 0, 0);
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
			matrices.push();
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90));
			MinecraftClient.getInstance().getItemRenderer().renderItem(piston, Mode.NONE, light, overlay, matrices, vcp);
			matrices.pop();
			ItemStack catalyst = catalysts.get((int)((System.currentTimeMillis()/1000)%catalysts.size()));
			matrices.translate(1, 0, 0);
			MinecraftClient.getInstance().getItemRenderer().renderItem(catalyst, Mode.NONE, light, overlay, matrices, vcp);
			matrices.translate(1+(1/16D), 0, 0);
			matrices.translate(1+(1/16D), 0, 0);
			MinecraftClient.getInstance().getItemRenderer().renderItem(catalyst, Mode.NONE, light, overlay, matrices, vcp);
			vcp.draw();
			matrices.pop();
		}));
		return widgets;
	}
	
}
