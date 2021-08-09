package com.unascribed.yttr.compat;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import com.google.common.collect.Lists;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.ClientHelper;
import me.shedaniel.rei.api.ConfigObject;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PistonSmashingCategory extends DrawableHelper implements RecipeCategory<PistonSmashingEntry> {

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
		int outX = cX+5;
		if (!recipe.getOutput().isEmpty()) {
			widgets.add(Widgets.createSlot(new Point(outX, cY - 22)).entry(recipe.getOutput()).markOutput());
			outX += 20;
		}
		if (!recipe.getCloudOutput().isEmpty()) {
			final int cloudX = outX;
			widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
				MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("minecraft", "textures/particle/effect_4.png"));
				RenderSystem.color3f(NativeImage.getBlue(recipe.getCloudColor())/255f, NativeImage.getGreen(recipe.getCloudColor())/255f, NativeImage.getRed(recipe.getCloudColor())/255f);
				DrawableHelper.drawTexture(matrices, cloudX, cY-16, 0, 0, 0, 8, 8, 8, 8);
				RenderSystem.color3f(1, 1, 1);
			}));
			outX += 14;
			ItemStack output = recipe.getCloudOutput().getItemStack().copy();
			if (!output.hasTag()) output.setTag(new CompoundTag());
			ListTag lore = new ListTag();
			lore.add(StringTag.of(Text.Serializer.toJson(new TranslatableText("category.yttr.piston_smashing.cloud_output_hint").setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.YELLOW)))));
			output.getOrCreateSubTag("display").put("Lore", lore);
			widgets.add(Widgets.createSlot(new Point(outX, cY - 22)).entry(EntryStack.create(output)).markOutput());
			outX += 20;
		}
		widgets.add(Widgets.createSlotBackground(new Point(cX - 18, cY)));
		widgets.add(Widgets.createTexturedWidget(new Identifier("yttr", "textures/gui/curved_arrow.png"), cX-15, cY-20, 0, 0, 16, 16, 16, 16));
		ItemStack piston = new ItemStack(Items.PISTON);
		List<Block> inputs = recipe.getInput();
		List<Block> catalysts = recipe.getCatalysts();
		widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
			VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			int light = LightmapTextureManager.pack(15, 15);
			int overlay = OverlayTexture.DEFAULT_UV;
			RenderSystem.color4f(1, 1, 1, 1);
			matrices.push();
			matrices.translate(cX-11, cY+8, 50);
			matrices.scale(16, -16, 16);
			matrices.translate(-2, 0, 0);
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
			matrices.push();
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90));
			MinecraftClient.getInstance().getItemRenderer().renderItem(piston, Mode.NONE, light, overlay, matrices, vcp);
			matrices.pop();
			BlockState catalyst = catalysts.get((int)((System.currentTimeMillis()/1000)%catalysts.size())).getDefaultState();
			BlockState input = inputs.get((int)((System.currentTimeMillis()/1000)%inputs.size())).getDefaultState();
			matrices.translate(0.5, -0.5, -0.5);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(catalyst, matrices, vcp, light, overlay);
			matrices.translate(1+(1/16D), 0, 0);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(input, matrices, vcp, light, overlay);
			matrices.translate(1+(1/16D), 0, 0);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(catalyst, matrices, vcp, light, overlay);
			vcp.draw();
			matrices.pop();
			Block hovered = null;
			// don't look at this
			int color = REIHelper.getInstance().isDarkThemeEnabled() ? -1877929711 : -2130706433;
			if (mouseX >= cX-18-16-1-16 && mouseX <= cX-18-1-16 && mouseY >= cY && mouseY <= cY+16) {
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				fillGradient(matrices, cX-18-16-1-16, cY, cX-18-1-16, cY+16, color, color);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
				hovered = Blocks.PISTON;
			} else if (mouseX >= cX-18-16-1 && mouseX <= cX-18-1 && mouseY >= cY && mouseY <= cY+16) {
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				fillGradient(matrices, cX-18-16-1, cY, cX-18-1, cY+16, color, color);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
				hovered = catalyst.getBlock();
			} else if (mouseX >= cX-18+16+1 && mouseX <= cX-18+16+16+1 && mouseY >= cY && mouseY <= cY+16) {
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				fillGradient(matrices, cX-18+16+1, cY, cX-18+16+16+1, cY+16, color, color);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
				hovered = catalyst.getBlock();
			} else if (mouseX >= cX-18 && mouseX <= cX-18+16 && mouseY >= cY && mouseY <= cY+16) {
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				fillGradient(matrices, cX-18, cY, cX-18+16, cY+16, color, color);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
				hovered = input.getBlock();
			}
			if (hovered != null) {
				Item item = hovered.asItem();
				if (item != Items.AIR) {
					ItemStack stack = new ItemStack(item);
					EntryStack es = EntryStack.create(stack);
					REIHelper.getInstance().queueTooltip(es.getTooltip(new Point(mouseX, mouseY)));
					boolean leftHeld = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS;
					boolean rightHeld = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 1) == GLFW.GLFW_PRESS;
					if ((ConfigObject.getInstance().getRecipeKeybind().getType() != InputUtil.Type.MOUSE && leftHeld) || ConfigObject.getInstance().getRecipeKeybind().matchesCurrentMouse() || ConfigObject.getInstance().getRecipeKeybind().matchesCurrentKey()) {
						ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addRecipesFor(es).setOutputNotice(es).fillPreferredOpenedCategory());
					} else if ((ConfigObject.getInstance().getUsageKeybind().getType() != InputUtil.Type.MOUSE && rightHeld) || ConfigObject.getInstance().getUsageKeybind().matchesCurrentMouse() || ConfigObject.getInstance().getUsageKeybind().matchesCurrentKey()) {
						ClientHelper.getInstance().openView(ClientHelper.ViewSearchBuilder.builder().addUsagesFor(es).setInputNotice(es).fillPreferredOpenedCategory());
					}
				} else {
					Identifier id = Registry.BLOCK.getId(hovered);
					List<Text> tooltip = Lists.newArrayList();
					tooltip.add(new TranslatableText("block."+id.getNamespace()+"."+id.getPath()));
					if (MinecraftClient.getInstance().options.advancedItemTooltips) {
						tooltip.add(new LiteralText(id.toString()).formatted(Formatting.DARK_GRAY));
					}
					ClientHelper.getInstance().appendModIdToTooltips(tooltip, id.getNamespace());
					REIHelper.getInstance().queueTooltip(Tooltip.create(tooltip));
				}
			}
		}));
		return widgets;
	}

}
