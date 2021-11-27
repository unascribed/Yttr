package com.unascribed.yttr.client;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

// extends RenderPhase for access to protected fields
public class YRenderLayers extends RenderPhase {

	private static final RenderPhase.Transparency ADDITIVE_WITH_ALPHA_TRANSPARENCY = new RenderPhase.Transparency("yttr_additive_transparency_with_alpha", () -> {
	      RenderSystem.enableBlend();
	      RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
	   }, () -> {
	      RenderSystem.disableBlend();
	      RenderSystem.defaultBlendFunc();
	   });
	
	private static final RenderLayer LAMP_HALO = RenderLayer.of("yttr_lamp_halo",
					VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
					GL11.GL_QUADS, 256, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true))
				.transparency(ADDITIVE_WITH_ALPHA_TRANSPARENCY)
				.writeMaskState(new RenderPhase.WriteMaskState(true, false))
				.layering(new RenderPhase.Layering("polygon_offset_layering_reverse", () -> {
						RenderSystem.polygonOffset(1, -10);
						RenderSystem.enablePolygonOffset();
					}, () -> {
						RenderSystem.polygonOffset(0, 0);
						RenderSystem.disablePolygonOffset();
					}))
				.build(false));
	
	private static final RenderLayer SHIFTER_LINES = RenderLayer.of("yttr_shifter_lines", VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, RenderLayer.MultiPhaseParameters.builder()
			.lineWidth(new LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.shadeModel(SMOOTH_SHADE_MODEL)
			.depthTest(LEQUAL_DEPTH_TEST)
			.target(ITEM_TARGET)
			.writeMaskState(COLOR_MASK)
			.build(false));
	
	private static final RenderLayer SHIFTER_LINES_HIDDEN = RenderLayer.of("yttr_shifter_lines", VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256, RenderLayer.MultiPhaseParameters.builder()
			.lineWidth(new LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.shadeModel(SMOOTH_SHADE_MODEL)
			.depthTest(ALWAYS_DEPTH_TEST)
			.target(ITEM_TARGET)
			.writeMaskState(COLOR_MASK)
			.build(false));
	
	public static RenderLayer getArmorTranslucentNoCull(Identifier tex) {
		return RenderLayer.of("yttr_armor_translucent_no_cull",
					VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
					GL11.GL_QUADS, 256, true, false,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(tex, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
				.alpha(ONE_TENTH_ALPHA)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.build(true));
	}
	
	public static RenderLayer getLampHalo() {
		return LAMP_HALO;
	}
	
	public static RenderLayer getShifterLines() {
		return SHIFTER_LINES;
	}
	
	public static RenderLayer getShifterLinesHidden() {
		return SHIFTER_LINES_HIDDEN;
	}
	
	private YRenderLayers() {
		super(null, null, null);
	}
	
}
