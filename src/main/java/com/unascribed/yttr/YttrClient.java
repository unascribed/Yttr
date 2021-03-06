package com.unascribed.yttr;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.PowerMeterBlockEntityRenderer;
import com.unascribed.yttr.mixin.AccessorEntityTrackingSoundInstance;

import com.google.common.collect.MapMaker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class YttrClient implements ClientModInitializer {
	
	private static final Identifier CHAMBER_TEXTURE = new Identifier("yttr", "textures/item/rifle_chamber.png");
	
	private static final ModelIdentifier BASE_MODEL = new ModelIdentifier("yttr:rifle_base#inventory");
	private static final ModelIdentifier CHAMBER_MODEL = new ModelIdentifier("yttr:rifle_chamber#inventory");
	private static final ModelIdentifier CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr:rifle_chamber_glass#inventory");
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.RIFLE, this::renderRifle);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(BASE_MODEL);
			out.accept(CHAMBER_MODEL);
			out.accept(CHAMBER_GLASS_MODEL);
		});
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if (sound.getSound().getIdentifier().equals(Yttr.RIFLE_CHARGE.getId()) && sound instanceof EntityTrackingSoundInstance) {
					rifleChargeSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "beam"), (client, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			int color = buf.readInt();
			float a = NativeImage.getAlpha(color)/255f;
			float r = NativeImage.getBlue(color)/255f;
			float g = NativeImage.getGreen(color)/255f;
			float b = NativeImage.getRed(color)/255f;
			float eX = buf.readFloat();
			float eY = buf.readFloat();
			float eZ = buf.readFloat();
			mc.send(() -> {
				Entity ent = mc.world.getEntityById(entityId);
				if (ent == null) return;
				boolean fp = ent == mc.player && mc.options.getPerspective() == Perspective.FIRST_PERSON;
				Vec3d start = RifleItem.getMuzzlePos(ent, fp);
				double len = MathHelper.sqrt(start.squaredDistanceTo(eX, eY, eZ));
				double diffX = eX-start.x;
				double diffY = eY-start.y;
				double diffZ = eZ-start.z;
				int count = (int)(len*14);
				DustParticleEffect eff = new DustParticleEffect(r, g, b, 0.2f);
				SpriteProvider sprites = ((ParticleManagerAccessor)mc.particleManager).getSpriteAwareFactories().get(Registry.PARTICLE_TYPE.getKey(ParticleTypes.DUST).get().getValue());
				for (int i = 0; i < count; i++) {
					double t = (i/(double)count);
					double x = start.x+(diffX*t);
					double y = start.y+(diffY*t);
					double z = start.z+(diffZ*t);
					final int fi = i;
					mc.particleManager.addParticle(new RedDustParticle(mc.world, x, y, z, 0, 0, 0, eff, sprites) {
						{
							if (fp && fi < 3) {
								scale /= 2;
							}
							setMaxAge((int)(Math.log10((fi*4)+5))+10);
							setColor(r, g, b);
							setColorAlpha(a);
							velocityX = 0;
							velocityY = 0;
							velocityZ = 0;
						}
						
						@Override
						protected int getColorMultiplier(float tint) {
							return LightmapTextureManager.pack(15, 15);
						}

						@Override
						public ParticleTextureSheet getType() {
							return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
						}
						
					});
				}
			});
		});
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.POWER_METER_ENTITY, PowerMeterBlockEntityRenderer::new);
	}
	
	public void renderRifle(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.pop();
		MinecraftClient mc = MinecraftClient.getInstance();
		boolean inUse = mc.player != null && mc.player.isUsingItem();
		int useTime = inUse ? mc.player.getItemUseTime() : 0;
		if (useTime > 80) {
			float a = (useTime-80)/40f;
			a = a*a;
			float f = 50;
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			matrices.translate((tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a);
		}
		BakedModel base = mc.getBakedModelManager().getModel(BASE_MODEL);
		BakedModel chamber = mc.getBakedModelManager().getModel(CHAMBER_MODEL);
		BakedModel chamberGlass = mc.getBakedModelManager().getModel(CHAMBER_GLASS_MODEL);
		boolean leftHanded = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.THIRD_PERSON_LEFT_HAND;
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, base);
		if (mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.FIRST_PERSON_RIGHT_HAND) {
			if (inUse) {
				RenderLayer layer = RenderLayer.getEntityCutoutNoCull(CHAMBER_TEXTURE);
				float[] minU = {2};
				float[] minV = {2};
				float[] maxU = {-1};
				float[] maxV = {-1};
				VertexConsumer dummy = new VertexConsumer() {
					
					@Override
					public VertexConsumer vertex(double x, double y, double z) {
						return this;
					}
					
					@Override
					public VertexConsumer texture(float u, float v) {
						minU[0] = Math.min(minU[0], u);
						maxU[0] = Math.max(maxU[0], u);
						minV[0] = Math.min(minV[0], v);
						maxV[0] = Math.max(maxV[0], v);
						return this;
					}
					
					@Override
					public VertexConsumer overlay(int u, int v) {
						return this;
					}
					
					@Override
					public VertexConsumer normal(float x, float y, float z) {
						return this;
					}
					
					@Override
					public void next() {
					}
					
					@Override
					public VertexConsumer light(int u, int v) {
						return this;
					}
					
					@Override
					public VertexConsumer color(int red, int green, int blue, int alpha) {
						return this;
					}
				};
				for (BakedQuad quad : chamber.getQuads(null, null, ThreadLocalRandom.current())) {
					dummy.quad(matrices.peek(), quad, 1, 1, 1, 1, 1);
				}
				int frame = mc.player.getItemUseTime() < 70 ? (int)((mc.player.getItemUseTime()/70f)*36) : 34+mc.player.getItemUseTime()%2;
				mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, new VertexConsumerProvider() {
					@Override
					public VertexConsumer getBuffer(RenderLayer junk) {
						VertexConsumer d = vertexConsumers.getBuffer(layer);
						return new VertexConsumer() {
							
							@Override
							public VertexConsumer vertex(double x, double y, double z) {
								d.vertex(x, y, z);
								return this;
							}
							
							@Override
							public VertexConsumer texture(float u, float v) {
								if (u >= minU[0] && u <= maxU[0]) {
									u = ((u-minU[0])/(maxU[0]-minU[0]));
								} else {
									System.out.println("U?? "+u+"; "+minU[0]+"/"+maxU[0]);
									u = 0;
								}
								if (v >= minV[0] && v <= maxV[0]) {
									v = ((frame*3)/108f)+(((v-minV[0])/(maxV[0]-minV[0]))*(3/108f));
								} else {
									System.out.println("V?? "+v+"; "+minV[0]+"/"+maxV[0]);
									v = 0;
								}
								d.texture(u, v);
								return this;
							}
							
							@Override
							public VertexConsumer overlay(int u, int v) {
								d.overlay(u, v);
								return this;
							}
							
							@Override
							public VertexConsumer normal(float x, float y, float z) {
								d.normal(x, y, z);
								return this;
							}
							
							@Override
							public void next() {
								d.next();
							}
							
							@Override
							public VertexConsumer light(int u, int v) {
								d.light(u, v);
								return this;
							}
							
							@Override
							public VertexConsumer color(int red, int green, int blue, int alpha) {
								int c = Yttr.RIFLE.getMode(stack).color;
								d.color(NativeImage.getBlue(c), NativeImage.getGreen(c), NativeImage.getRed(c), 255);
								return this;
							}
						};
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
	
}
