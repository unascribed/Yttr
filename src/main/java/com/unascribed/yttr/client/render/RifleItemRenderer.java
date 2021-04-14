package com.unascribed.yttr.client.render;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.item.RifleItem;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class RifleItemRenderer extends IHasAClient {
	
	private static final Identifier CHAMBER_TEXTURE = new Identifier("yttr", "textures/item/rifle_chamber.png");
	
	private static final ModelIdentifier RIFLE_BASE_MODEL = new ModelIdentifier("yttr:rifle_base#inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_MODEL = new ModelIdentifier("yttr:rifle_chamber#inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr:rifle_chamber_glass#inventory");
	
	private static final UVObserver uvo = new UVObserver();

	public static void renderRifle(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		float tickDelta = mc.getTickDelta();
		matrices.pop();
		boolean fp = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.FIRST_PERSON_RIGHT_HAND;
		boolean inUse = fp && mc.player != null && mc.player.isUsingItem();
		float useTime = inUse ? ((RifleItem)stack.getItem()).calcAdjustedUseTime(stack, mc.player.getItemUseTimeLeft()-tickDelta) : 0;
		if (useTime > 80) {
			float a = (useTime-80)/40f;
			a = a*a;
			if (stack.getItem() == YItems.RIFLE_REINFORCED) {
				a /= 3;
			}
			float f = 50;
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			matrices.translate((tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a);
		}
		BakedModel base = mc.getBakedModelManager().getModel(RIFLE_BASE_MODEL);
		BakedModel chamber = mc.getBakedModelManager().getModel(RIFLE_CHAMBER_MODEL);
		BakedModel chamberGlass = mc.getBakedModelManager().getModel(RIFLE_CHAMBER_GLASS_MODEL);
		boolean leftHanded = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.THIRD_PERSON_LEFT_HAND;
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, base);
		if (fp) {
			if (inUse) {
				RenderLayer layer = RenderLayer.getEntityCutoutNoCull(CHAMBER_TEXTURE);
				uvo.reset();
				for (BakedQuad quad : chamber.getQuads(null, null, ThreadLocalRandom.current())) {
					uvo.quad(matrices.peek(), quad, 1, 1, 1, 1, 1);
				}
				float minU = uvo.getMinU();
				float minV = uvo.getMinV();
				float maxU = uvo.getMaxU();
				float maxV = uvo.getMaxV();
				int frame = useTime < 70 ? (int)((useTime/70f)*36) : 34+(int)(mc.world.getTime()%2);
				mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, junk -> new DelegatingVertexConsumer(vertexConsumers.getBuffer(layer)) {
					@Override
					public VertexConsumer texture(float u, float v) {
						if (u >= minU && u <= maxU) {
							u = ((u-minU)/(maxU-minU));
						} else {
							System.out.println("U?? "+u+"; "+minU+"/"+maxU);
							u = 0;
						}
						if (v >= minV && v <= maxV) {
							v = ((frame*3)/108f)+(((v-minV)/(maxV-minV))*(3/108f));
						} else {
							System.out.println("V?? "+v+"; "+minV+"/"+maxV);
							v = 0;
						}
						return super.texture(u, v);
					}
					
					@Override
					public VertexConsumer color(int red, int green, int blue, int alpha) {
						int c = ((RifleItem)stack.getItem()).getMode(stack).color;
						return super.color(NativeImage.getBlue(c), NativeImage.getGreen(c), NativeImage.getRed(c), 255);
					}
				}, light, overlay, chamber);
				if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw(layer);
			}
		}
		RenderSystem.disableCull();
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, chamberGlass);
		if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		RenderSystem.enableCull();
		matrices.push();
	}

	public static void registerModels(Consumer<Identifier> out) {
		out.accept(RIFLE_BASE_MODEL);
		out.accept(RIFLE_CHAMBER_MODEL);
		out.accept(RIFLE_CHAMBER_GLASS_MODEL);
	}
	
}
