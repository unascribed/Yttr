package com.unascribed.yttr.client;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.unascribed.yttr.util.YLog;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.EmbeddedResourcePack;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.render.CleaverUI;
import com.unascribed.yttr.client.render.EffectorRenderer;
import com.unascribed.yttr.client.render.ReplicatorRenderer;
import com.unascribed.yttr.client.render.RifleHUDRenderer;
import com.unascribed.yttr.client.render.ShifterUI;
import com.unascribed.yttr.client.render.SuitHUDRenderer;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.content.block.big.BigBlock;
import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlock;
import com.unascribed.yttr.content.block.void_.DivingPlateBlock;
import com.unascribed.yttr.content.block.void_.DormantVoidGeyserBlock;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YEntities;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixin.accessor.client.AccessorClientPlayerInteractionManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityTrackingSoundInstance;
import com.unascribed.yttr.mixin.accessor.client.AccessorResourcePackManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorVertexBuffer;
import com.unascribed.yttr.util.annotate.ConstantColor;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockRenderView;

public class YttrClient extends IHasAClient implements ClientModInitializer {
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	public static final Map<Entity, SoundInstance> dropCastSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	private final List<Identifier> additionalSprites = Lists.newArrayList();
	
	private boolean hasCheckedRegistry = false;
	
	@Override
	public void onInitializeClient() {
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			additionalSprites.forEach(registry::register);
		});
		doReflectionMagic();
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
				} else if ((sound.getSound().getIdentifier().equals(YSounds.DROP_CAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					SoundInstance existing = dropCastSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
					if (existing != null) {
						mc.getSoundManager().stop(existing);
					}
				}
			});
			ReloadableResourceManager rm = (ReloadableResourceManager)mc.getResourceManager();
			rm.registerReloader(reloader("yttr:clear_thief_cache", (manager) -> TextureColorThief.clearCache()));
			rm.registerReloader(reloader("yttr:detect_lcah", (manager) -> Yttr.lessCreepyAwareHopper = manager.containsResource(new Identifier("yttr", "lcah-marker"))));
			Yttr.lessCreepyAwareHopper = rm.containsResource(new Identifier("yttr", "lcah-marker"));
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
					hasCheckedRegistry = true;
					for (Map.Entry<RegistryKey<Block>, Block> en : Registry.BLOCK.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
							if (en.getValue() instanceof ReplicatorBlock) continue;
							if (en.getValue() instanceof DivingPlateBlock) continue;
							if (en.getValue() instanceof DormantVoidGeyserBlock) continue;
							if (en.getValue() instanceof CleavedBlock) continue;
							if (en.getValue().getDefaultState().isAir()) continue;
							if (en.getValue().getLootTableId().equals(LootTables.EMPTY)) continue;
							if (!mc.getServer().getLootManager().getTableIds().contains(en.getValue().getLootTableId())) {
								if (en.getValue().getDroppedStacks(en.getValue().getDefaultState(), new LootContext.Builder(mc.getServer().getOverworld())
										.parameter(LootContextParameters.TOOL, new ItemStack(Items.APPLE))
										.parameter(LootContextParameters.ORIGIN, Vec3d.ZERO)).isEmpty()) {
									YLog.error("Block "+en.getKey().getValue()+" is missing a loot table and doesn't seem to have custom drops");
								}
							}
						}
					}
					for (Map.Entry<RegistryKey<Item>, Item> en : Registry.ITEM.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
					for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> en : Registry.ENTITY_TYPE.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
				}
			}
			if (mc.isPaused()) return;
			EffectorRenderer.tick();
			SuitHUDRenderer.tick();
			ReplicatorRenderer.tick();
			RifleHUDRenderer.tick();
			ShifterUI.tick();
			if (mc.player != null && mc.player.isCreative() && mc.player.getStackInHand(Hand.MAIN_HAND).getItem() == YItems.SHIFTER) {
				((AccessorClientPlayerInteractionManager)mc.interactionManager).yttr$setBlockBreakingCooldown(0);
			}
		});
		
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			SuitHUDRenderer.render(matrixStack, tickDelta);
			RifleHUDRenderer.render(matrixStack, tickDelta);
			ShifterUI.render(matrixStack, tickDelta);
		});
		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
		WorldRenderEvents.BLOCK_OUTLINE.register(ReplicatorRenderer::renderOutline);
		WorldRenderEvents.BLOCK_OUTLINE.register(ShifterUI::renderOutline);
		WorldRenderEvents.BLOCK_OUTLINE.register((wrc, boc) -> {
			if (boc.blockState().getBlock() instanceof BigBlock) {
				BlockState bs = boc.blockState();
				BigBlock b = (BigBlock)boc.blockState().getBlock();
				double minX = boc.blockPos().getX()-bs.get(b.X);
				double minY = boc.blockPos().getY()-bs.get(b.Y);
				double minZ = boc.blockPos().getZ()-bs.get(b.Z);
				minX -= wrc.camera().getPos().x;
				minY -= wrc.camera().getPos().y;
				minZ -= wrc.camera().getPos().z;
				double maxX = minX+b.xSize;
				double maxY = minY+b.ySize;
				double maxZ = minZ+b.zSize;
				VertexConsumer vc = wrc.consumers().getBuffer(RenderLayer.getLines());
				WorldRenderer.drawBox(wrc.matrixStack(), vc, minX, minY, minZ, maxX, maxY, maxZ, 0, 0, 0, 0.4f);
				return false;
			}
			return true;
		});
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

	private SimpleSynchronousResourceReloadListener reloader(String idStr, Consumer<ResourceManager> cb) {
		Identifier id = new Identifier(idStr);
		return new SimpleSynchronousResourceReloadListener() {
			@Override
			public void reload(ResourceManager manager) {
				cb.accept(manager);
			}
			
			@Override
			public Identifier getFabricId() {
				return id;
			}
		};
	}

	private void checkTranslation(Identifier id, String key) {
		if (!I18n.hasTranslation(key)) {
			YLog.error("Translation "+key+" is missing for "+id);
		}
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
	
	@SuppressWarnings("unchecked")
	private void doReflectionMagic() {
		Map<String, RenderLayer> renderLayers = Maps.newHashMap();
		renderLayers.put("cutout", RenderLayer.getCutout());
		renderLayers.put("cutout_mipped", RenderLayer.getCutoutMipped());
		renderLayers.put("translucent", RenderLayer.getTranslucent());
		Yttr.eachRegisterableField(YBlocks.class, Block.class, com.unascribed.yttr.util.annotate.RenderLayer.class, (f, b, ann) -> {
			if (b instanceof BlockColorProvider) ColorProviderRegistry.BLOCK.register((BlockColorProvider)b, b);
			if (ann != null) {
				if (!renderLayers.containsKey(ann.value())) throw new RuntimeException("YBlocks."+f.getName()+" has an unknown @RenderLayer: "+ann.value());
				BlockRenderLayerMap.INSTANCE.putBlocks(renderLayers.get(ann.value()), b);
			}
		});
		Yttr.eachRegisterableField(YItems.class, Item.class, null, (f, i, ann) -> {
			if (i instanceof ItemColorProvider) ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
			ConstantColor colAnn = f.getAnnotation(ConstantColor.class);
			if (colAnn != null) ColorProviderRegistry.ITEM.register((stack, tintIndex) -> colAnn.value(), i);
			YItems.SimpleArmorTexture satAnn = f.getAnnotation(YItems.SimpleArmorTexture.class);
			if (satAnn != null) ArmorRenderingRegistry.registerSimpleTexture(new Identifier(satAnn.value()), i);
			YItems.BuiltinRenderer birAnn = f.getAnnotation(YItems.BuiltinRenderer.class);
			if (birAnn != null) {
				try {
					MethodHandle renderHandle = MethodHandles.publicLookup().findStatic(birAnn.value(), "render", MethodType.methodType(void.class, ItemStack.class, Mode.class, MatrixStack.class, VertexConsumerProvider.class, int.class, int.class));
					BuiltinItemRendererRegistry.INSTANCE.register(i, (is, mode, matrices, vcp, light, overlay) -> {
						try {
							renderHandle.invoke(is, mode, matrices, vcp, light, overlay);
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
					try {
						MethodHandle registerModelsHandle = MethodHandles.publicLookup().findStatic(birAnn.value(), "registerModels", MethodType.methodType(void.class, Consumer.class));
						ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
							try {
								registerModelsHandle.invoke(out);
							} catch (RuntimeException | Error e) {
								throw e;
							} catch (Throwable e) {
								throw new RuntimeException(e);
							}
						});
					} catch (NoSuchMethodException e) {
						// ignore
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		Yttr.eachRegisterableField(YBlockEntities.class, BlockEntityType.class, YBlockEntities.Renderer.class, (f, type, ann) -> {
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
		Yttr.eachRegisterableField(YEntities.class, EntityType.class, YEntities.Renderer.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(ann.value(), MethodType.methodType(void.class, EntityRenderDispatcher.class));
					EntityRendererRegistry.INSTANCE.register(type, (erd, ctx) -> {
						try {
							return (EntityRenderer)handle.invoke(erd);
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
		Map<Fluid, Class<?>> fluids = Maps.newHashMap();
		Map<Class<?>, Identifier[]> fluidSprites = Maps.newHashMap();
		Map<Class<?>, int[]> fluidColors = Maps.newHashMap();
		Yttr.eachRegisterableField(YFluids.class, Fluid.class, null, (f, fl, ann) -> {
			fluids.put(fl, f.getType());
			com.unascribed.yttr.util.annotate.RenderLayer rlAnn = f.getAnnotation(com.unascribed.yttr.util.annotate.RenderLayer.class);
			if (rlAnn != null) {
				if (!renderLayers.containsKey(rlAnn.value())) throw new RuntimeException("YFluids."+f.getName()+" has an unknown @RenderLayer: "+rlAnn.value());
				BlockRenderLayerMap.INSTANCE.putFluids(renderLayers.get(rlAnn.value()), fl);
			}
			ConstantColor colAnn = f.getAnnotation(ConstantColor.class);
			if (colAnn != null) {
				if (!fluidColors.containsKey(f.getType().getSuperclass())) {
					fluidColors.put(f.getType().getSuperclass(), new int[] { -1, -1 });
				}
				fluidColors.get(f.getType().getSuperclass())[fl.isStill(fl.getDefaultState()) ? 0 : 1] = colAnn.value();
			}
			YFluids.Sprite spriteAnn = f.getAnnotation(YFluids.Sprite.class);
			if (spriteAnn != null) {
				if (!fluidSprites.containsKey(f.getType().getSuperclass())) {
					fluidSprites.put(f.getType().getSuperclass(), new Identifier[] { new Identifier("missingno"), new Identifier("missingno") });
				}
				fluidSprites.get(f.getType().getSuperclass())[fl.isStill(fl.getDefaultState()) ? 0 : 1] = new Identifier(spriteAnn.value());
			}
		});
		int[] white = new int[] { -1, -1 };
		Identifier[] missingno = { new Identifier("missingno"), new Identifier("missingno") };
		for (Map.Entry<Fluid, Class<?>> en : fluids.entrySet()) {
			final int[] colors = fluidColors.getOrDefault(en.getValue().getSuperclass(), white);
			final Identifier[] spriteIds = fluidSprites.getOrDefault(en.getValue().getSuperclass(), missingno);
			additionalSprites.add(spriteIds[0]);
			additionalSprites.add(spriteIds[1]);
			FluidRenderHandler frh = new FluidRenderHandler() {
				@Override
				public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
					return new Sprite[] {
						mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(spriteIds[0]),
						mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(spriteIds[1])
					};
				}
				@Override
				public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
					return state.isStill() ? colors[0] : colors[1];
				}
			};
			FluidRenderHandlerRegistry.INSTANCE.register(en.getKey(), frh);
		}
		Yttr.eachRegisterableField(YHandledScreens.class, ScreenHandlerType.class, YHandledScreens.Screen.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					Constructor<?> actualConstructor = null;
					for (Constructor<?> cons : ann.value().getConstructors()) {
						if (cons.getParameterCount() == 3 && ScreenHandler.class.isAssignableFrom(cons.getParameterTypes()[0])) {
							actualConstructor = cons;
						}
					}
					if (actualConstructor == null) throw new RuntimeException(ann.value().getSimpleName()+" does not have a normal constructor");
					MethodHandle handle = MethodHandles.publicLookup().unreflectConstructor(actualConstructor);
					// must be an anonymous class due to type unsafety; we need the rawtype
					ScreenRegistry.register(type, new ScreenRegistry.Factory() {

						@Override
						public Screen create(ScreenHandler handler, PlayerInventory inventory, Text title) {
							try {
								return (HandledScreen)handle.invoke(handler, inventory, title);
							} catch (RuntimeException | Error e) {
								throw e;
							} catch (Throwable e) {
								throw new RuntimeException(e);
							}
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
}
