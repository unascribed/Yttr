package com.unascribed.yttr;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.google.gson.internal.UnsafeAllocator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.AwareHopperBlockEntityRenderer;
import com.unascribed.yttr.client.DummyServerWorld;
import com.unascribed.yttr.client.LampBlockEntityRenderer;
import com.unascribed.yttr.client.LevitationChamberBlockEntityRenderer;
import com.unascribed.yttr.client.PowerMeterBlockEntityRenderer;
import com.unascribed.yttr.client.SqueezedLeavesBlockEntityRenderer;
import com.unascribed.yttr.client.TextureColorThief;
import com.unascribed.yttr.client.VoidBallParticle;
import com.unascribed.yttr.mixin.AccessorEntityRendererDispatcher;
import com.unascribed.yttr.mixin.AccessorEntityTrackingSoundInstance;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import com.google.common.hash.Hashing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public class YttrClient implements ClientModInitializer {
	
	private static final Identifier CHAMBER_TEXTURE = new Identifier("yttr", "textures/item/rifle_chamber.png");
	
	private static final Identifier VOID_FLOW = new Identifier("yttr", "block/void_flow");
	private static final Identifier VOID_STILL = new Identifier("yttr", "block/void_still");
	
	private static final ModelIdentifier BASE_MODEL = new ModelIdentifier("yttr:rifle_base#inventory");
	private static final ModelIdentifier CHAMBER_MODEL = new ModelIdentifier("yttr:rifle_chamber#inventory");
	private static final ModelIdentifier CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr:rifle_chamber_glass#inventory");
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	private final UVObserver uvo = new UVObserver();
	
	private static final Supplier<World> dummyWorld;
	static {
		Supplier<World> dummyWorldTemp;
		try {
			DummyServerWorld sw = UnsafeAllocator.create().newInstance(DummyServerWorld.class);
			sw.init();
			dummyWorldTemp = () -> sw;
		} catch (Exception e) {
			dummyWorldTemp = () -> MinecraftClient.getInstance().world;
			LogManager.getLogger("Yttr").warn("Failed to construct dummy ServerWorld, using client world directly. Snare color determination may be wrong for some entities!", e);
		}
		dummyWorld = dummyWorldTemp;
	}
	private static final Cache<CompoundTag, Identifier> textureCache = CacheBuilder.newBuilder()
			.expireAfterAccess(5, TimeUnit.SECONDS)
			.build();
	
	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.RIFLE, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.RIFLE_REINFORCED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.RIFLE_OVERCLOCKED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.LAMP, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.FIXTURE, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(Yttr.CAGE_LAMP, this::renderLamp);
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(VOID_FLOW);
			registry.register(VOID_STILL);
		});
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
			Yttr.CHUTE,
			Yttr.LEVITATION_CHAMBER,
			Yttr.SQUEEZE_LEAVES,
			Yttr.SQUEEZED_LEAVES,
			Yttr.SQUEEZE_SAPLING);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			Yttr.LAMP,
			Yttr.FIXTURE,
			Yttr.CAGE_LAMP);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
			Yttr.GLASSY_VOID,
			Yttr.DELICACE_BLOCK,
			Yttr.GLASSY_VOID_PANE);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(BASE_MODEL);
			out.accept(CHAMBER_MODEL);
			out.accept(CHAMBER_GLASS_MODEL);
		});
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			int baseColor;
			if (stack.getItem() == Yttr.RIFLE_REINFORCED) {
				baseColor = 0x223333;
			} else if (stack.getItem() == Yttr.RIFLE_OVERCLOCKED) {
				baseColor = 0x111111;
			} else {
				baseColor = 0x3E5656;
			}
			if (tintIndex == 0) return baseColor;
			tintIndex--;
			RifleMode mode = ((RifleItem)stack.getItem()).getMode(stack);
			float ammo = (((RifleItem)stack.getItem()).getRemainingAmmo(stack)/(float)(((RifleItem)stack.getItem()).getMaxAmmo(stack)))*6;
			int ammoI = (int)ammo;
			if (ammoI > tintIndex) return mode.color;
			float a = ammoI < tintIndex ? 1 : 1-(ammo%1);
			float rF = NativeImage.getBlue(mode.color)/255f;
			float gF = NativeImage.getGreen(mode.color)/255f;
			float bF = NativeImage.getRed(mode.color)/255f;
			float rE = (((baseColor>>16)&0xFF)/255f)+0.05f;
			float gE = (((baseColor>>8)&0xFF)/255f)+0.05f;
			float bE = ((baseColor&0xFF)/255f)+0.15f;
			float r = rF+((rE-rF)*a);
			float g = gF+((gE-gF)*a);
			float b = bF+((bE-bF)*a);
			return NativeImage.getAbgrColor(255, (int)(r*255), (int)(g*255), (int)(b*255));
		}, Yttr.RIFLE, Yttr.RIFLE_REINFORCED, Yttr.RIFLE_OVERCLOCKED);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (tintIndex == 0) return -1;
			EntityType<?> type = Yttr.SNARE.getEntityType(stack);
			if (type != null) {
				int primary;
				int secondary;
				CompoundTag data = stack.getTag().getCompound("Contents");
				if (!textureCache.asMap().containsKey(data)) {
					if (type == EntityType.FALLING_BLOCK) {
						BlockState bs = NbtHelper.toBlockState(data.getCompound("BlockState"));
						BakedModel bm = MinecraftClient.getInstance().getBlockRenderManager().getModel(bs);
						Identifier id = bm.getSprite().getId();
						textureCache.put(data, new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
					} else if (type == EntityType.ITEM) {
						ItemStack item = ItemStack.fromTag(data.getCompound("Item"));
						BakedModel bm = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(item);
						Identifier id = bm.getSprite().getId();
						textureCache.put(data, new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
					} else {
						EntityRenderer renderer = ((AccessorEntityRendererDispatcher)MinecraftClient.getInstance().getEntityRenderDispatcher()).yttr$getRenderers().get(type);
						if (renderer == null) {
							textureCache.put(data, TextureColorThief.MISSINGNO);
						} else {
							try {
								textureCache.put(data, renderer.getTexture(Yttr.SNARE.createEntity(dummyWorld.get(), stack)));
							} catch (Throwable e) {
								LogManager.getLogger("Yttr").warn("Failed to determine color for entity", e);
								textureCache.put(data, TextureColorThief.MISSINGNO);
							}
						}
					}
				}
				Identifier tex = textureCache.getIfPresent(data);
				if (tex != null && tex != TextureColorThief.MISSINGNO) {
					primary = TextureColorThief.getPrimaryColor(tex);
					secondary = TextureColorThief.getSecondaryColor(tex);
				} else {
					SpawnEggItem spi = SpawnEggItem.forEntity(type);
					if (spi != null) {
						primary = spi.getColor(0);
						secondary = spi.getColor(1);
					} else {
						primary = Hashing.murmur3_32().hashString(Registry.ENTITY_TYPE.getId(type).toString(), Charsets.UTF_8).asInt();
						secondary = ~primary;
					}
				}
				return tintIndex == 1 ? primary : secondary;
			} else {
				return ((ThreadLocalRandom.current().nextInt(100)+155)<<16)|(ThreadLocalRandom.current().nextInt(64)<<8);
			}
		}, Yttr.SNARE);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			int waterColor = world.getColor(pos, BiomeColors.WATER_COLOR);
			int waterR = (waterColor >> 16)&0xFF;
			int waterG = (waterColor >>  8)&0xFF;
			int waterB = (waterColor >>  0)&0xFF;
			int leafR = waterB;
			int leafG = waterB-(waterR/4);
			int leafB = waterG-(waterB/3);
			if (!state.get(Properties.WATERLOGGED)) {
				leafR = leafR*2/3;
				leafG = leafG*2/3;
				leafB = leafB*2/3;
			}
			int leafColor = (leafR<<16) | (leafG<<8) | (leafB);
			return leafColor;
		}, Yttr.SQUEEZE_LEAVES, Yttr.SQUEEZED_LEAVES);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			LampColor color = state.get(LampBlock.COLOR);
			return state.get(LampBlock.LIT) ? color.baseLitColor : color.baseUnlitColor;
		}, Yttr.LAMP, Yttr.FIXTURE, Yttr.CAGE_LAMP);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			return 0xFFFFEE58;
		}, Yttr.SQUEEZE_LEAVES);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			LampColor color = LampBlockItem.getColor(stack);
			return LampBlockItem.isInverted(stack) ? color.baseLitColor : color.baseUnlitColor;
		}, Yttr.LAMP, Yttr.FIXTURE, Yttr.CAGE_LAMP);
		FluidRenderHandler voidRenderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				MinecraftClient mc = MinecraftClient.getInstance();
				return new Sprite[] {
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(VOID_STILL),
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(VOID_FLOW)
				};
			}
			@Override
			public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return 0xFFAAAAAA;
			}
		};
		FluidRenderHandlerRegistry.INSTANCE.register(Yttr.VOID, voidRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(Yttr.FLOWING_VOID, voidRenderHandler);
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if ((sound.getSound().getIdentifier().equals(Yttr.RIFLE_CHARGE.getId()) || sound.getSound().getIdentifier().equals(Yttr.RIFLE_CHARGE_FAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					rifleChargeSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
				}
			});
			((ReloadableResourceManager)mc.getResourceManager()).registerListener(new SimpleSynchronousResourceReloadListener() {

				@Override
				public Identifier getFabricId() {
					return new Identifier("yttr", "clear_thief_cache");
				}

				@Override
				public void apply(ResourceManager manager) {
					TextureColorThief.clearCache();
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "beam"), (client, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			int color = buf.readInt();
			// NativeImage assumes little-endian, but our colors are big-endian, so swap red/blue
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
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "void_ball"), (client, handler, buf, responseSender) -> {
			float x = buf.readFloat();
			float y = buf.readFloat();
			float z = buf.readFloat();
			float r = buf.readFloat();
			mc.send(() -> {
				mc.particleManager.addParticle(new VoidBallParticle(mc.world, x, y, z, r));
			});
		});
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.POWER_METER_ENTITY, PowerMeterBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.AWARE_HOPPER_ENTITY, AwareHopperBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.LEVITATION_CHAMBER_ENTITY, LevitationChamberBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.SQUEEZED_LEAVES_ENTITY, SqueezedLeavesBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(Yttr.LAMP_ENTITY, LampBlockEntityRenderer::new);
		FabricModelPredicateProviderRegistry.register(Yttr.SNARE, new Identifier("yttr", "filled"), (stack, world, entity) -> {
			return stack.hasTag() && stack.getTag().contains("Contents") ? 1 : 0;
		});
		FabricModelPredicateProviderRegistry.register(Blocks.AIR.asItem(), new Identifier("yttr", "halo"), (stack, world, entity) -> {
			return retrievingHalo ? 1 : 0;
		});
		
		WorldRenderEvents.BLOCK_OUTLINE.register((wrc, boc) -> {
			ItemStack held = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND);
			if (held.getItem() instanceof CleaverItem) {
				CleaverItem ci = (CleaverItem)held.getItem();
				HitResult tgt = MinecraftClient.getInstance().crosshairTarget;
				if (tgt instanceof BlockHitResult && (!ci.requiresSneaking() || boc.entity().isSneaking())) {
					BlockPos cleaving = ci.getCleaveBlock(held);
					if (cleaving == null && tgt.getPos().squaredDistanceTo(boc.cameraX(), boc.cameraY(), boc.cameraZ()) > 2*2) return true;
					BlockPos pos = cleaving == null ? boc.blockPos() : cleaving;
					BlockState bs = wrc.world().getBlockState(pos);
					if (bs.isSolidBlock(wrc.world(), pos)) {
						GlStateManager.pushMatrix();
						GlStateManager.multMatrix(wrc.matrixStack().peek().getModel());
						GlStateManager.translated(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
						GlStateManager.disableTexture();
						RenderSystem.defaultBlendFunc();
						GlStateManager.enableBlend();
						GL11.glEnable(GL11.GL_POINT_SMOOTH);
						GL11.glEnable(GL11.GL_LINE_SMOOTH);
						float scale = (float)MinecraftClient.getInstance().getWindow().getScaleFactor();
						int sd = CleaverItem.SUBDIVISIONS;
						Vec3d cleaveStart = ci.getCleaveStart(held);
						boolean anySelected = false;
						float selectedX = 0;
						float selectedY = 0;
						float selectedZ = 0;
						for (int x = 0; x <= sd; x++) {
							for (int y = 0; y <= sd; y++) {
								for (int z = 0; z <= sd; z++) {
									if ((x > 0 && x < sd) &&
											(y > 0 && y < sd) &&
											(z > 0 && z < sd)) {
										continue;
									}
									
									float wX = x/(float)sd;
									float wY = y/(float)sd;
									float wZ = z/(float)sd;
									boolean isStart = cleaveStart != null && cleaveStart.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05;
									boolean selected = false;
									float a;
									if (!isStart) {
										double dist = tgt.getPos().squaredDistanceTo(pos.getX()+wX, pos.getY()+wY, pos.getZ()+wZ);
										final double maxDist = 0.75;
										if (dist > maxDist*maxDist) continue;
										selected = dist < 0.1*0.1;
										double distSq = Math.sqrt(dist);
										a = (float)((maxDist-distSq)/maxDist);
									} else {
										a = 1;
									}
									float r = 1;
									float g = 1;
									float b = 1;
									float size = a*10;
									if (isStart) {
										size = 8;
										g = 0;
										b = 0;
									} else if (selected) {
										size = 15;
										b = 0;
										anySelected = true;
										selectedX = wX;
										selectedY = wY;
										selectedZ = wZ;
									}
									GL11.glPointSize(size*scale);
									GlStateManager.color4f(r, g, b, a);
									GL11.glBegin(GL11.GL_POINTS);
									GL11.glVertex3f(wX, wY, wZ);
									GL11.glEnd();
								}
							}
						}
						if (anySelected && cleaveStart != null) {
							GlStateManager.color4f(1, 0.5f, 0, 0.5f);
							GL11.glLineWidth(4*scale);
							GL11.glBegin(GL11.GL_LINES);
							GL11.glVertex3d(cleaveStart.x, cleaveStart.y, cleaveStart.z);
							GL11.glVertex3f(selectedX, selectedY, selectedZ);
							GL11.glEnd();
						}
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
						GlStateManager.enableTexture();
						return true;
					}
				}
			}
			return true;
		});
	}
	
	public static boolean retrievingHalo = false;
	
	public void renderRifle(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		float tickDelta = MinecraftClient.getInstance().getTickDelta();
		matrices.pop();
		MinecraftClient mc = MinecraftClient.getInstance();
		boolean fp = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.FIRST_PERSON_RIGHT_HAND;
		boolean inUse = fp && mc.player != null && mc.player.isUsingItem();
		float useTime = inUse ? ((RifleItem)stack.getItem()).calcAdjustedUseTime(stack, mc.player.getItemUseTimeLeft()-tickDelta) : 0;
		if (useTime > 80) {
			float a = (useTime-80)/40f;
			a = a*a;
			if (stack.getItem() == Yttr.RIFLE_REINFORCED) {
				a /= 3;
			}
			float f = 50;
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			matrices.translate((tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a);
		}
		BakedModel base = mc.getBakedModelManager().getModel(BASE_MODEL);
		BakedModel chamber = mc.getBakedModelManager().getModel(CHAMBER_MODEL);
		BakedModel chamberGlass = mc.getBakedModelManager().getModel(CHAMBER_GLASS_MODEL);
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
	
	private final LampBlockEntityRenderer lampItemGlow = new LampBlockEntityRenderer(null);
	
	public void renderLamp(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BlockState state = ((BlockItem)stack.getItem()).getBlock().getDefaultState()
				.with(LampBlock.LIT, LampBlockItem.isInverted(stack))
				.with(LampBlock.COLOR, LampBlockItem.getColor(stack));
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
		matrices.translate(-0.5, -0.5, -0.5);
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
        int i = MinecraftClient.getInstance().getBlockColors().getColor(state, null, null, 0);
        float r = (i >> 16 & 255) / 255.0F;
        float g = (i >> 8 & 255) / 255.0F;
        float b = (i & 255) / 255.0F;
        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(matrices.peek(),
        		vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), state, model, r, g, b, light, overlay);
        if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		lampItemGlow.render(MinecraftClient.getInstance().world, null, state, matrices, vertexConsumers, light, overlay);
	}
	
}
