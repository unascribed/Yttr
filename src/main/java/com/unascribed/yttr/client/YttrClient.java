package com.unascribed.yttr.client;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.EmbeddedResourcePack;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.block.mechanism.ReplicatorBlock;
import com.unascribed.yttr.client.particle.VoidBallParticle;
import com.unascribed.yttr.client.render.CleaverUI;
import com.unascribed.yttr.client.render.EffectorRenderer;
import com.unascribed.yttr.client.render.LampItemRenderer;
import com.unascribed.yttr.client.render.ReplicatorItemRenderer;
import com.unascribed.yttr.client.render.ReplicatorRenderer;
import com.unascribed.yttr.client.render.RifleItemRenderer;
import com.unascribed.yttr.client.render.SuitHUDRenderer;
import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.client.screen.handled.CentrifugeScreen;
import com.unascribed.yttr.client.screen.handled.SuitStationScreen;
import com.unascribed.yttr.client.screen.handled.VoidFilterScreen;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.client.suit.SuitSound;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YScreenTypes;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.item.EffectorItem;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityTrackingSoundInstance;
import com.unascribed.yttr.mixin.accessor.client.AccessorResourcePackManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorVertexBuffer;
import com.unascribed.yttr.util.annotate.ConstantColor;
import com.unascribed.yttr.util.annotate.Renderer;
import com.unascribed.yttr.world.Geyser;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.toast.Toast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockRenderView;

public class YttrClient extends IHasAClient implements ClientModInitializer {
	
	public static final Identifier VOID_FLOW = new Identifier("yttr", "block/void_flow");
	public static final Identifier VOID_STILL = new Identifier("yttr", "block/void_still");
	
	public static final Identifier PURE_VOID_FLOW = new Identifier("yttr", "block/pure_void_flow");
	public static final Identifier PURE_VOID_STILL = new Identifier("yttr", "block/pure_void_still");
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	private boolean hasCheckedRegistry = false;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE, RifleItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_REINFORCED, RifleItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_OVERCLOCKED, RifleItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.LAMP, LampItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.FIXTURE, LampItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.CAGE_LAMP, LampItemRenderer::render);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.REPLICATOR, ReplicatorItemRenderer::render);
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(VOID_FLOW);
			registry.register(VOID_STILL);
			registry.register(PURE_VOID_FLOW);
			registry.register(PURE_VOID_STILL);
		});
		Map<String, RenderLayer> renderLayers = Maps.newHashMap();
		//for (RenderLayer layer : RenderLayer.getBlockLayers()) {
		//	renderLayers.put(((AccessorRenderPhase)layer).yttr$getName(), layer);
		//}
		// Iris does something unspeakable to render layers. Hardcode it.
		renderLayers.put("cutout", RenderLayer.getCutout());
		renderLayers.put("cutout_mipped", RenderLayer.getCutoutMipped());
		renderLayers.put("translucent", RenderLayer.getTranslucent());
		eachRegisterableField(YBlocks.class, Block.class, com.unascribed.yttr.util.annotate.RenderLayer.class, (f, b, ann) -> {
			if (b instanceof BlockColorProvider) ColorProviderRegistry.BLOCK.register((BlockColorProvider)b, b);
			if (ann != null) {
				if (!renderLayers.containsKey(ann.value())) throw new RuntimeException("YBlocks."+f.getName()+" has an unknown @RenderLayer: "+ann.value());
				BlockRenderLayerMap.INSTANCE.putBlocks(renderLayers.get(ann.value()), b);
			}
		});
		eachRegisterableField(YItems.class, Item.class, ConstantColor.class, (f, i, ann) -> {
			if (i instanceof ItemColorProvider) ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
			if (ann != null) ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ann.value(), i);
		});
		eachRegisterableField(YBlockEntities.class, BlockEntityType.class, Renderer.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(ann.value(), MethodType.methodType(void.class, BlockEntityRenderDispatcher.class));
					BlockEntityRendererRegistry.INSTANCE.register(type, berd -> {
						try {
							return (BlockEntityRenderer<?>)handle.invoke(berd);
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), YFluids.PURE_VOID, YFluids.FLOWING_PURE_VOID);
		
		ScreenRegistry.register(YScreenTypes.CENTRIFUGE, CentrifugeScreen::new);
		ScreenRegistry.register(YScreenTypes.SUIT_STATION, SuitStationScreen::new);
		ScreenRegistry.register(YScreenTypes.VOID_FILTER, VoidFilterScreen::new);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			RifleItemRenderer.registerModels(out);
		});
		registerFluidRenderers();
		ArmorRenderingRegistry.registerSimpleTexture(new Identifier("yttr", "suit"),
				YItems.SUIT_HELMET, YItems.SUIT_CHESTPLATE, YItems.SUIT_LEGGINGS, YItems.SUIT_BOOTS);
		ArmorRenderingRegistry.registerSimpleTexture(new Identifier("yttr", "goggles"),
				YItems.GOGGLES);
		ArmorRenderingRegistry.registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) -> {
			String namespace = "minecraft";
			String name = "diamond";
			if (stack.hasTag() && stack.getTag().getInt("yttr:DurabilityBonus") > 0) {
				namespace = "yttr";
				name = "ultrapure_diamond";
			}
			return new Identifier(namespace, "textures/models/armor/" + name + "_layer_" + (secondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png");
		}, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if ((sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE.getId()) || sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE_FAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					SoundInstance existing = rifleChargeSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
					if (existing != null) {
						mc.getSoundManager().stop(existing);
					}
				}
			});
			ReloadableResourceManager rm = (ReloadableResourceManager)mc.getResourceManager();
			rm.registerListener(new SimpleSynchronousResourceReloadListener() {

				@Override
				public Identifier getFabricId() {
					return new Identifier("yttr", "clear_thief_cache");
				}

				@Override
				public void apply(ResourceManager manager) {
					TextureColorThief.clearCache();
				}
			});
			rm.registerListener(new SimpleSynchronousResourceReloadListener() {

				@Override
				public Identifier getFabricId() {
					return new Identifier("yttr", "detect_lcah");
				}

				@Override
				public void apply(ResourceManager manager) {
					Yttr.lessCreepyAwareHopper = manager.containsResource(new Identifier("yttr", "lcah-marker"));
				}
			});
			Yttr.lessCreepyAwareHopper = rm.containsResource(new Identifier("yttr", "lcah-marker"));
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "beam"), this::handleBeamPacket);
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "void_ball"), (client, handler, buf, responseSender) -> {
			float x = buf.readFloat();
			float y = buf.readFloat();
			float z = buf.readFloat();
			float r = buf.readFloat();
			mc.send(() -> {
				mc.particleManager.addParticle(new VoidBallParticle(mc.world, x, y, z, r));
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "effector"), (client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			Direction dir = Direction.byId(buf.readUnsignedByte());
			int dist = buf.readUnsignedByte();
			mc.send(() -> {
				BlockPos endPos = pos.offset(dir, dist);
				client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, pos));
				client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, pos), 130);
				for (int i = 0; i < dist; i += 4) {
					BlockPos midPos = pos.offset(dir, i);
					client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, midPos));
					client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, midPos), 130);
				}
				client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, endPos));
				client.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, endPos), 130);
				EffectorRenderer.addHole(pos, dir, dist);
				EffectorItem.effect(client.world, pos, dir, null, null, dist, false);
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive"), (client, handler, buf, responseSender) -> {
			int x = buf.readInt();
			int z = buf.readInt();
			List<Geyser> geysers = Lists.newArrayList();
			while (buf.isReadable()) {
				geysers.add(Geyser.read(buf));
			}
			mc.send(() -> {
				client.openScreen(new SuitScreen(x, z, geysers));
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_pos"), (client, handler, buf, responseSender) -> {
			int x = buf.readInt();
			int z = buf.readInt();
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					((SuitScreen)client.currentScreen).setPos(x, z);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "discovered_geyser"), (client, handler, buf, responseSender) -> {
			Geyser g = Geyser.read(buf);
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					((SuitScreen)client.currentScreen).addGeyser(g);
				} else {
					String name = g.name;
					client.getToastManager().add((matrices, manager, startTime) -> {
						manager.getGame().getTextureManager().bindTexture(Toast.TEXTURE);
						manager.drawTexture(matrices, 0, 0, 0, 0, 160, 32);
						manager.getGame().getTextureManager().bindTexture(SuitRenderer.SUIT_TEX);
						DrawableHelper.drawTexture(matrices, 4, 4, 23, 18, 12, 12, SuitRenderer.SUIT_TEX_WIDTH, SuitRenderer.SUIT_TEX_HEIGHT);
						manager.getGame().textRenderer.draw(matrices, "§l"+I18n.translate("yttr.geyser_discovered"), 30, 7, -1);
						manager.getGame().textRenderer.draw(matrices, name, 30, 18, -1);
						return startTime >= 5000 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
					});
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "animate_fastdive"), (client, handler, buf, responseSender) -> {
			Multiset<SuitResource> costs = EnumMultiset.create(SuitResource.class);
			for (SuitResource sr : SuitResource.VALUES) {
				costs.add(sr, buf.readVarInt());
			}
			int x = buf.readVarInt();
			int z = buf.readVarInt();
			int time = buf.readVarInt();
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					((SuitScreen)client.currentScreen).startFastDive(costs, x, z, time);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_end"), (client, handler, buf, responseSender) -> {
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					client.getSoundManager().play(new SuitSound(YSounds.DIVE_END));
					client.openScreen(null);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "dive_pressure"), (client, handler, buf, responseSender) -> {
			int pressure = buf.readVarInt();
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					((SuitScreen)client.currentScreen).setPressure(pressure);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "cant_dive"), (client, handler, buf, responseSender) -> {
			String msg = buf.readString();
			mc.send(() -> {
				if (client.currentScreen instanceof SuitScreen) {
					((SuitScreen)client.currentScreen).showError(msg);
				}
			});
		});
		
		
		FabricModelPredicateProviderRegistry.register(YItems.SNARE, new Identifier("yttr", "filled"), (stack, world, entity) -> {
			return stack.hasTag() && stack.getTag().contains("Contents") ? 1 : 0;
		});
		FabricModelPredicateProviderRegistry.register(Blocks.AIR.asItem(), new Identifier("yttr", "halo"), (stack, world, entity) -> {
			return retrievingHalo ? 1 : 0;
		});
		FabricModelPredicateProviderRegistry.register(new Identifier("yttr", "durability_bonus"), (stack, world, entity) -> {
			return stack.hasTag() ? stack.getTag().getInt("yttr:DurabilityBonus") : 0;
		});
		
		ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
				if (mc.world != null && mc.isIntegratedServerRunning() && !hasCheckedRegistry) {
					Logger log = LogManager.getLogger("YttrRegistryCheck");
					hasCheckedRegistry = true;
					for (Map.Entry<RegistryKey<Block>, Block> en : Registry.BLOCK.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(log, en.getKey().getValue(), en.getValue().getTranslationKey());
							if (en.getValue() instanceof ReplicatorBlock) continue;
							if (!mc.getServer().getLootManager().getTableIds().contains(en.getValue().getLootTableId())) {
								if (en.getValue().getDroppedStacks(en.getValue().getDefaultState(), new LootContext.Builder(mc.getServer().getOverworld())
										.parameter(LootContextParameters.TOOL, new ItemStack(Items.APPLE))
										.parameter(LootContextParameters.ORIGIN, Vec3d.ZERO)).isEmpty()) {
									log.error("Block "+en.getKey().getValue()+" is missing a loot table and doesn't seem to have custom drops");
								}
							}
						}
					}
					for (Map.Entry<RegistryKey<Item>, Item> en : Registry.ITEM.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(log, en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
					for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> en : Registry.ENTITY_TYPE.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(log, en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
				}
			}
			if (mc.isPaused()) return;
			EffectorRenderer.tick();
			SuitHUDRenderer.tick();
			ReplicatorRenderer.tick();
		});
		
		HudRenderCallback.EVENT.register(SuitHUDRenderer::render);
		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
		WorldRenderEvents.BLOCK_OUTLINE.register(ReplicatorRenderer::renderOutline);
		WorldRenderEvents.LAST.register(EffectorRenderer::render);
		WorldRenderEvents.LAST.register(ReplicatorRenderer::render);
		CleavedBlockModelProvider.init();
		
		ResourcePackProvider prov = new ResourcePackProvider() {
			@Override
			public void register(Consumer<ResourcePackProfile> consumer, Factory factory) {
				Supplier<ResourcePack> f = () -> new EmbeddedResourcePack("lcah");
				consumer.accept(factory.create("", false, f, f.get(),
						new PackResourceMetadata(new LiteralText("Makes the Aware Hopper less creepy."), 6),
						InsertionPosition.TOP, ResourcePackSource.method_29486("Yttr built-in")));
			}
		};
		
		AccessorResourcePackManager arpm = ((AccessorResourcePackManager)MinecraftClient.getInstance().getResourcePackManager());
		Set<ResourcePackProvider> providers = arpm.yttr$getProviders();
		try {
			providers.add(prov);
		} catch (UnsupportedOperationException e) {
			providers = Sets.newHashSet(providers);
			providers.add(prov);
			arpm.yttr$setProviders(providers);
		}
	}

	private void checkTranslation(Logger log, Identifier id, String key) {
		if (!I18n.hasTranslation(key)) {
			log.error("Translation "+key+" is missing for "+id);
		}
	}

	private void registerFluidRenderers() {
		FluidRenderHandler voidRenderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
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
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.VOID, voidRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.FLOWING_VOID, voidRenderHandler);
		
		FluidRenderHandler pureVoidRenderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return new Sprite[] {
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(PURE_VOID_STILL),
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(PURE_VOID_FLOW)
				};
			}
			@Override
			public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return 0xFFFFFFFF;
			}
		};
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.PURE_VOID, pureVoidRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.FLOWING_PURE_VOID, pureVoidRenderHandler);
	}

	private <T, A extends Annotation> void eachRegisterableField(Class<?> holder, Class<T> type, Class<A> anno, TriConsumer<Field, T, A> cb) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					cb.accept(f, (T)f.get(null), anno == null ? null : f.getAnnotation(anno));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void handleBeamPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
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
	}
	
	public static boolean retrievingHalo = false;

	public static void drawBufferWithoutClobberingGLMatrix(VertexBuffer buf, Matrix4f mat, int mode) {
		if (mat != null) {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(mat);
		}
		RenderSystem.drawArrays(mode, 0, ((AccessorVertexBuffer)buf).yttr$getVertexCount());
		if (mat != null) {
			RenderSystem.popMatrix();
		}
	}
	
}
