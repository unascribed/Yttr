package com.unascribed.yttr.client.render;

import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.content.item.ShifterItem;
import com.unascribed.yttr.network.MessageC2SShifterMode;
import com.unascribed.yttr.util.math.Interp;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ShifterUI extends IHasAClient {

	private static Set<BlockPos> lastPositions = null;
	private static VoxelShape lastShape = null;
	
	public static boolean renderOutline(WorldRenderContext wrc, BlockOutlineContext boc) {
		if (mc.player != null) {
			ItemStack held = mc.player.getStackInHand(Hand.MAIN_HAND);
			if (held.getItem() instanceof ShifterItem) {
				ShifterItem si = (ShifterItem)held.getItem();
				VoxelShape shanpe = VoxelShapes.empty();
				Set<BlockPos> positions = si.getAffectedBlocks(mc.player, mc.world, boc.blockPos(), mc.crosshairTarget instanceof BlockHitResult ? ((BlockHitResult)mc.crosshairTarget).getSide() : Direction.UP,
						held.hasTag() && held.getTag().getBoolean("ReplaceDisconnected"),
						held.hasTag() && held.getTag().getBoolean("ReplaceHidden"),
						held.hasTag() && held.getTag().getBoolean("PlaneRestrict"));
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
						float t = mc.player == null ? 0 : mc.player.age+wrc.tickDelta();
						float h1 = (float) ((((x1+y1+z1)+(t/5))/20)%1);
						if (h1 < 0) h1 += 1;
						float h2 = (float) ((((x2+y2+z2)+(t/5))/20)%1);
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
	
	public static final int ANIM_TIME = 5;
	public static final float ANIM_TIMEf = ANIM_TIME;
	
	private static final Identifier MODES = new Identifier("yttr", "textures/gui/shifter_modes.png");
	
	private static int ticksSinceOpen = -1;
	private static int ticksSinceClose = -1;
	
	private static int ticksSinceChangeDisconnected = 1000;
	private static int ticksSinceChangeHidden = 1000;
	private static int ticksSinceChangePlane = 1000;
	
	private static ItemStack shifterStack;
	private static ShifterItem shifterItem;
	
	public static void render(MatrixStack matrices, float delta) {
		if (mc.player == null || shifterStack == null || shifterItem == null) return;
		if (ticksSinceOpen > 0 || (ticksSinceClose != -1 && ticksSinceClose < ANIM_TIME)) {
			float mainA = Interp.sCurve5((Math.min((ticksSinceOpen > 0 ? ticksSinceOpen+delta : ANIM_TIME-(ticksSinceClose+delta)), ANIM_TIME)/ANIM_TIMEf));
			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			matrices.push();
				matrices.translate(mc.getWindow().getScaledWidth()/2, mc.getWindow().getScaledHeight()/2, 0);
				RenderSystem.color4f(1, 1, 1, mainA);
				mc.getTextureManager().bindTexture(MODES);
				boolean disconnected = shifterStack.hasTag() && shifterStack.getTag().getBoolean("ReplaceDisconnected");
				boolean hidden = shifterStack.hasTag() && shifterStack.getTag().getBoolean("ReplaceHidden");
				boolean plane = shifterStack.hasTag() && shifterStack.getTag().getBoolean("PlaneRestrict");
				DrawableHelper.drawTexture(matrices, -24, -8, 0, disconnected ? 16 : 0, 16, 16, 48, 32);
				DrawableHelper.drawTexture(matrices, -8, -24, 16, hidden ? 16 : 0, 16, 16, 48, 32);
				DrawableHelper.drawTexture(matrices, 8, -8, 32, plane ? 16 : 0, 16, 16, 48, 32);
				if (ticksSinceChangeDisconnected < ANIM_TIME) {
					RenderSystem.color4f(1, 1, 1, 1-Interp.sCurve5((ticksSinceChangeDisconnected+delta)/ANIM_TIMEf)*mainA);
					DrawableHelper.drawTexture(matrices, -24, -8, 0, disconnected ? 0 : 16, 16, 16, 48, 32);
				}
				if (ticksSinceChangeHidden < ANIM_TIME) {
					RenderSystem.color4f(1, 1, 1, 1-Interp.sCurve5((ticksSinceChangeHidden+delta)/ANIM_TIMEf)*mainA);
					DrawableHelper.drawTexture(matrices, -8, -24, 16, hidden ? 0 : 16, 16, 16, 48, 32);
				}
				if (ticksSinceChangePlane < ANIM_TIME) {
					RenderSystem.color4f(1, 1, 1, 1-Interp.sCurve5((ticksSinceChangePlane+delta)/ANIM_TIMEf)*mainA);
					DrawableHelper.drawTexture(matrices, 8, -8, 32, plane ? 0 : 16, 16, 16, 48, 32);
				}
			matrices.pop();
			RenderSystem.enableAlphaTest();
		}
	}
	
	public static void tick() {
		if (mc.player == null) return;
		ItemStack stack = mc.player.getMainHandStack();
		if (!(stack.getItem() instanceof ShifterItem)) {
			if ((ticksSinceOpen == -1 && ticksSinceClose == -1) || ticksSinceClose > ANIM_TIME) {
				ticksSinceOpen = -1;
				ticksSinceClose = -1;
				shifterStack = null;
				shifterItem = null;
			} else {
				if (ticksSinceOpen > 0) ticksSinceOpen = -1;
				ticksSinceClose++;
			}
			return;
		}
		shifterStack = stack;
		shifterItem = (ShifterItem)stack.getItem();
		if (mc.options.keySwapHands.isPressed() || mc.options.keySwapHands.wasPressed()) {
			if (ticksSinceOpen == -1) {
				ticksSinceOpen = 0;
				ticksSinceClose = -1;
			} else {
				ticksSinceOpen++;
				ticksSinceClose = -1;
			}
			boolean disconnected = stack.hasTag() && stack.getTag().getBoolean("ReplaceDisconnected");
			boolean hidden = stack.hasTag() && stack.getTag().getBoolean("ReplaceHidden");
			boolean plane = stack.hasTag() && stack.getTag().getBoolean("PlaneRestrict");
			boolean changed = false;
			// drain timesPressed to prevent vanilla behavior
			while (mc.options.keySwapHands.wasPressed()) {}
			if (mc.options.keyAttack.wasPressed()) {
				while (mc.options.keyAttack.wasPressed()) {}
				mc.options.keyAttack.setPressed(false);
				disconnected = !disconnected;
				ticksSinceChangeDisconnected = 0;
				changed = true;
			}
			if (mc.options.keyPickItem.wasPressed()) {
				while (mc.options.keyPickItem.wasPressed()) {}
				mc.options.keyPickItem.setPressed(false);
				hidden = !hidden;
				ticksSinceChangeHidden = 0;
				changed = true;
			}
			if (mc.options.keyUse.wasPressed()) {
				while (mc.options.keyUse.wasPressed()) {}
				mc.options.keyUse.setPressed(false);
				plane = !plane;
				ticksSinceChangePlane = 0;
				changed = true;
			}
			if (changed) {
				if (!stack.hasTag()) stack.setTag(new NbtCompound());
				stack.getTag().putBoolean("ReplaceDisconnected", disconnected);
				stack.getTag().putBoolean("ReplaceHidden", hidden);
				stack.getTag().putBoolean("PlaneRestrict", plane);
				new MessageC2SShifterMode(disconnected, hidden, plane).sendToServer();
			}
		} else if (ticksSinceOpen > 0 || ticksSinceClose >= 0) {
			ticksSinceOpen = -1;
			if (ticksSinceClose == -1) {
				ticksSinceClose = 0;
			} else {
				ticksSinceClose++;
			}
		}
		ticksSinceChangeDisconnected++;
		ticksSinceChangeHidden++;
		ticksSinceChangePlane++;
	}
	
}
