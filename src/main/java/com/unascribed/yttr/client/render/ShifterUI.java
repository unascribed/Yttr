package com.unascribed.yttr.client.render;

import java.util.Set;

import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.content.item.ShifterItem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ShifterUI extends IHasAClient {

	private static Set<BlockPos> lastPositions = null;
	private static VoxelShape lastShape = null;
	
	public static boolean render(WorldRenderContext wrc, BlockOutlineContext boc) {
		if (mc.player != null) {
			ItemStack held = mc.player.getStackInHand(Hand.MAIN_HAND);
			if (held.getItem() instanceof ShifterItem) {
				ShifterItem si = (ShifterItem)held.getItem();
				VoxelShape shanpe = VoxelShapes.empty();
				Set<BlockPos> positions = si.getAffectedBlocks(mc.player, mc.world, boc.blockPos(), held.hasTag() && held.getTag().getBoolean("ReplaceHidden"));
				if (lastPositions != null && lastPositions.equals(positions)) {
					shanpe = lastShape;
				} else {
					for (BlockPos bp : positions) {
						VoxelShape thisShape = wrc.world().getBlockState(bp).getOutlineShape(wrc.world(), bp);
						thisShape = thisShape.offset(bp.getX(), bp.getY(), bp.getZ());
						shanpe = VoxelShapes.combine(shanpe, thisShape, BooleanBiFunction.OR);
					}
				}
				lastPositions = positions;
				lastShape = shanpe;
				Matrix4f matrix4f = wrc.matrixStack().peek().getModel();
				VertexConsumerProvider.Immediate vcp = mc.getBufferBuilders().getEntityVertexConsumers();vcp.draw(YRenderLayers.getShifterLines());
				for (int p = 0; p < 2; p++) {
					// ideally we'd do this in one pass, but since we're using a custom render layer, everything gets lumped together if we do that
					RenderLayer l = p == 0 ? YRenderLayers.getShifterLines() : YRenderLayers.getShifterLinesHidden();
					int a = (p == 0 ? 255 : 96);
					VertexConsumer vc = vcp.getBuffer(l);
					double x = -wrc.camera().getPos().x;
					double y = -wrc.camera().getPos().y;
					double z = -wrc.camera().getPos().z;
					shanpe.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
						float t = mc.player.age+wrc.tickDelta();
						float h1 = (float) ((((x1+y1+z1)+(t/3))/40)%1);
						if (h1 < 0) h1 += 1;
						float h2 = (float) ((((x2+y2+z2)+(t/3))/40)%1);
						if (h2 < 0) h2 += 1;
						int c1 = MathHelper.hsvToRgb(h1, 0.3f, 1);
						int c2 = MathHelper.hsvToRgb(h2, 0.3f, 1);
						vc.vertex(matrix4f, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z)).color((c1 >> 16)&0xFF, (c1 >> 8)&0xFF, c1&0xFF, a).next();
						vc.vertex(matrix4f, (float)(x2 + x), (float)(y2 + y), (float)(z2 + z)).color((c2 >> 16)&0xFF, (c2 >> 8)&0xFF, c2&0xFF, a).next();
					});
					vcp.draw(l);
				}
				return true;
			}
		}
		return true;
	}
	
}
