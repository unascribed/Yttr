package com.unascribed.yttr.client;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.EffectorWorld;
import com.unascribed.yttr.SuitResource;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.annotate.ConstantColor;
import com.unascribed.yttr.annotate.Renderer;
import com.unascribed.yttr.block.DivingPlateBlock;
import com.unascribed.yttr.block.LampBlock;
import com.unascribed.yttr.block.entity.VoidGeyserBlockEntity;
import com.unascribed.yttr.client.particle.VoidBallParticle;
import com.unascribed.yttr.client.render.LampBlockEntityRenderer;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YScreenTypes;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.item.EffectorItem;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.item.SuitArmorItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityTrackingSoundInstance;
import com.unascribed.yttr.mixin.accessor.client.AccessorRenderPhase;
import com.unascribed.yttr.world.Geyser;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;

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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class YttrClient implements ClientModInitializer {
	
	private static final Identifier CHAMBER_TEXTURE = new Identifier("yttr", "textures/item/rifle_chamber.png");
	
	private static final Identifier VOID_FLOW = new Identifier("yttr", "block/void_flow");
	private static final Identifier VOID_STILL = new Identifier("yttr", "block/void_still");
	
	private static final ModelIdentifier RIFLE_BASE_MODEL = new ModelIdentifier("yttr:rifle_base#inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_MODEL = new ModelIdentifier("yttr:rifle_chamber#inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr:rifle_chamber_glass#inventory");
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	private final UVObserver uvo = new UVObserver();
	
	private final MinecraftClient mc = MinecraftClient.getInstance();
	
	private final List<EffectorHole> effectorHoles = Lists.newArrayList();
	
	private SuitRenderer diveReadyRenderer = new SuitRenderer();
	private int diveReadyTime = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_REINFORCED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_OVERCLOCKED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.LAMP, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.FIXTURE, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.CAGE_LAMP, this::renderLamp);
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(VOID_FLOW);
			registry.register(VOID_STILL);
		});
		eachRegisterableField(YBlocks.class, Block.class, (f, b) -> {
			if (b instanceof BlockColorProvider) {
				ColorProviderRegistry.BLOCK.register((BlockColorProvider)b, b);
			}
			com.unascribed.yttr.annotate.RenderLayer ann = f.getAnnotation(com.unascribed.yttr.annotate.RenderLayer.class);
			if (ann != null) {
				boolean foundIt = false;
				for (RenderLayer layer : RenderLayer.getBlockLayers()) {
					if (((AccessorRenderPhase)layer).yttr$getName().equals(ann.value())) {
						BlockRenderLayerMap.INSTANCE.putBlocks(layer, b);
						foundIt = true;
						break;
					}
				}
				if (!foundIt) throw new RuntimeException("YBlocks."+f.getName()+" has an unknown @RenderLayer: "+ann.value());
			}
		});
		eachRegisterableField(YItems.class, Item.class, (f, i) -> {
			if (i instanceof ItemColorProvider) {
				ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
			}
			ConstantColor ann = f.getAnnotation(ConstantColor.class);
			if (ann != null) {
				ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ann.value(), i);
			}
		});
		eachRegisterableField(YBlockEntities.class, BlockEntityType.class, (f, type) -> {
			Renderer ann = f.getAnnotation(Renderer.class);
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
		ScreenRegistry.register(YScreenTypes.CENTRIFUGE, CentrifugeScreen::new);
		ScreenRegistry.register(YScreenTypes.SUIT_STATION, SuitStationScreen::new);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(RIFLE_BASE_MODEL);
			out.accept(RIFLE_CHAMBER_MODEL);
			out.accept(RIFLE_CHAMBER_GLASS_MODEL);
		});
		registerFluidRenderers();
		ArmorRenderingRegistry.registerSimpleTexture(new Identifier("yttr", "suit"),
				YItems.SUIT_HELMET, YItems.SUIT_CHESTPLATE, YItems.SUIT_LEGGINGS, YItems.SUIT_BOOTS);
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if ((sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE.getId()) || sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE_FAST.getId()))
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
				effectorHoles.add(new EffectorHole(pos, dir, dist));
				EffectorItem.effect(client.world, pos, dir, null, dist, false);
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
		
		ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
			if (mc.isPaused()) return;
			Iterator<EffectorHole> iter = effectorHoles.iterator();
			while (iter.hasNext()) {
				if (iter.next().age++ > 150) {
					iter.remove();
				}
			}
			if (mc.player != null && Yttr.isWearingFullSuit(mc.player) && Yttr.isStandingOnDivingPlate(mc.player)) {
				VoidGeyserBlockEntity geyser = DivingPlateBlock.findClosestGeyser(mc.world, mc.player.getBlockPos());
				if (geyser != null && mc.player.getBoundingBox().intersects(new Box(geyser.getPos()).expand(5))) {
					diveReadyTime++;
					if (diveReadyRenderer != null) diveReadyRenderer.tick();
				} else {
					diveReadyTime = 0;
					diveReadyRenderer = null;
				}
			} else {
				diveReadyTime = 0;
				diveReadyRenderer = null;
			}
		});
		
		HudRenderCallback.EVENT.register((matrices, delta) -> {
			if (diveReadyTime > 0) {
				if (diveReadyRenderer == null) {
					diveReadyRenderer = new SuitRenderer();
				}
				diveReadyRenderer.setUp();
				diveReadyRenderer.setColor(LampBlockItem.getColor(mc.player.getEquippedStack(EquipmentSlot.HEAD)));
				int width = mc.getWindow().getScaledWidth();
				String text = "hold sneak to dive";
				ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
				if (chest.getItem() instanceof SuitArmorItem) {
					SuitArmorItem sai = (SuitArmorItem)chest.getItem();
					int resourceBarY = 32;
					for (SuitResource res : SuitResource.VALUES) {
						int len = res.name().length()*6;
						
						String name = res.name().toLowerCase(Locale.ROOT);
						
						float amt = sai.getResourceAmount(chest, res);
						float a = amt/res.getMaximum();
						if (a < 0.5f) {
							diveReadyRenderer.drawElement(matrices, name+"-warning", width-96, resourceBarY-2, 0, 18, 11, 12, delta);
						}
						if (a <= 0 && res != SuitResource.FUEL) {
							text = "hold sneak to die";
						}
						
						diveReadyRenderer.drawText(matrices, name, width-len-16, resourceBarY, delta);
						diveReadyRenderer.drawBar(matrices, name, width-96, resourceBarY+12, a, true, delta);
						resourceBarY += 24;
					}
				}
				diveReadyRenderer.drawText(matrices, text, width-16-(text.length()*6), 12, delta);
				diveReadyRenderer.tearDown();
			}
		});
		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
		WorldRenderEvents.LAST.register((wrc) -> {
			if (effectorHoles.isEmpty()) return;
			ClientWorld w = wrc.world();
			if (!(w instanceof EffectorWorld)) return;
			EffectorWorld ew = (EffectorWorld)w;
			Vec3d cam = wrc.camera().getPos();
			MatrixStack ms = new MatrixStack();
			ms.translate(-cam.x, -cam.y, -cam.z);
			BlockPos.Mutable mut = new BlockPos.Mutable();
			List<Axis> axes = Arrays.asList(Direction.Axis.values());
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();
			for (EffectorHole hole : effectorHoles) {
				Axis axisZ = hole.dir.getAxis();
				Axis axisX = Iterables.find(axes, a -> a != axisZ);
				Axis axisY = Iterables.find(Lists.reverse(axes), a -> a != axisZ);
				float t = hole.age+wrc.tickDelta();
				float a;
				if (t <= 4) {
					a = 1-sCurve5(1-(t/4));
				} else if (t >= 130) {
					a = sCurve5((150-t)/20);
				} else {
					a = 1;
				}
				if (a < 0.05) a = 0;
				if (a > 0.95) a = 1;
				if (a != 1) {
					drawVoidCap(w, ms, mut, hole.length, axisX, axisY, a, hole.start, hole.dir);
					drawVoidCap(w, ms, mut, 0, axisX, axisY, a, hole.start.offset(hole.dir, hole.length-1), hole.dir.getOpposite());
				}
				bb.begin(GL11.GL_QUADS, RenderLayer.getSolid().getVertexFormat());
				for (int z = 0; z < hole.length; z++) {
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -2);
					EffectorItem.move(mut, axisX, -1);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisY, AxisDirection.POSITIVE));
						EffectorItem.move(mut, axisX, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, 2);
					EffectorItem.move(mut, axisX, -1);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisY, AxisDirection.NEGATIVE));
						EffectorItem.move(mut, axisX, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -1);
					EffectorItem.move(mut, axisX, -2);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisX, AxisDirection.POSITIVE));
						EffectorItem.move(mut, axisY, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -1);
					EffectorItem.move(mut, axisX, 2);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisX, AxisDirection.NEGATIVE));
						EffectorItem.move(mut, axisY, 1);
					}
				}
				GlStateManager.depthMask(false);
				GlStateManager.disableTexture();
				GlStateManager.enablePolygonOffset();
				GlStateManager.polygonOffset(-3, -3);
				tess.draw();
				GlStateManager.enableTexture();
				GlStateManager.depthMask(true);
				GlStateManager.depthFunc(GL11.GL_LESS);
				GlStateManager.disablePolygonOffset();
			}
		});
	}

	private void drawVoidCap(ClientWorld w, MatrixStack ms, BlockPos.Mutable mut, int l, Axis axisX, Axis axisY, float a, BlockPos pos, Direction dir) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		ms.push();
		ms.translate(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
		ms.translate(dir.getOffsetX()*-0.5, dir.getOffsetY()*-0.5, dir.getOffsetZ()*-0.5);
		ms.multiply(dir.getRotationQuaternion());
		Matrix4f mat = ms.peek().getModel();
		if (a != 0) {
			float s = a*1.5f;
			GlStateManager.disableTexture();
			if (l > 0) {
				GlStateManager.enableBlend();
				RenderSystem.defaultBlendFunc();
				GlStateManager.color4f(0, 0, 0, a > 0.75f ? (1-a)*4 : 1);
				GlStateManager.depthMask(false);
				bb.begin(GL11.GL_QUADS, VertexFormats.POSITION);
				bb.vertex(mat,  s, 0,  s).next();
				bb.vertex(mat,  s, l,  s).next();
				bb.vertex(mat,  s, l, -s).next();
				bb.vertex(mat,  s, 0, -s).next();
	
				bb.vertex(mat, -s, 0, -s).next();
				bb.vertex(mat, -s, l, -s).next();
				bb.vertex(mat, -s, l,  s).next();
				bb.vertex(mat, -s, 0,  s).next();
				
				bb.vertex(mat, -s, 0,  s).next();
				bb.vertex(mat, -s, l,  s).next();
				bb.vertex(mat,  s, l,  s).next();
				bb.vertex(mat,  s, 0,  s).next();
	
				bb.vertex(mat,  s, 0, -s).next();
				bb.vertex(mat,  s, l, -s).next();
				bb.vertex(mat, -s, l, -s).next();
				bb.vertex(mat, -s, 0, -s).next();
				tess.draw();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
			}
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.enablePolygonOffset();
			GlStateManager.polygonOffset(-3, -3);
			GlStateManager.disableCull();
			bb.begin(GL11.GL_QUADS, VertexFormats.POSITION);
			bb.vertex(mat, -s, 0, -s).next();
			bb.vertex(mat,  s, 0, -s).next();
			bb.vertex(mat,  s, 0,  s).next();
			bb.vertex(mat, -s, 0,  s).next();
			tess.draw();
			GlStateManager.disablePolygonOffset();
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.color4f(1, 1, 1, 1);
			GlStateManager.enableCull();
			GlStateManager.enableTexture();
		}
		ms.pop();
		mc.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		bb.begin(GL11.GL_QUADS, RenderLayer.getSolid().getVertexFormat());
		DiffuseLighting.enable();
		Random r = new Random();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				mut.set(pos);
				EffectorItem.move(mut, axisX, x);
				EffectorItem.move(mut, axisY, y);
				int sky = w.getLightLevel(LightType.SKY, mut);
				int block = w.getLightLevel(LightType.BLOCK, mut);
				int light = LightmapTextureManager.pack(block, sky);
				BlockState state = mc.world.getBlockState(mut);
				BakedModel model = mc.getBlockRenderManager().getModel(state);
				if (model == null) continue;
				r.setSeed(42);
				Iterable<BakedQuad> quads = model.getQuads(state, dir.getOpposite(), r);
				if (quads == null) continue;
				ms.push();
				ms.translate(mut.getX(), mut.getY(), mut.getZ());
				for (BakedQuad q : quads) {
					int color = q.hasColor() ? mc.getBlockColors().getColor(state, w, pos, q.getColorIndex()) : -1;
					bb.quad(ms.peek(), q, ((color >> 16)&0xFF)/255f, ((color >> 8)&0xFF)/255f, (color&0xFF)/255f, light, OverlayTexture.DEFAULT_UV);
				}
				ms.pop();
			}
		}
		tess.draw();
		DiffuseLighting.disable();
		GlStateManager.colorMask(false, false, false, false);
		GlStateManager.disableCull();
		GlStateManager.disableTexture();
		bb.begin(GL11.GL_QUADS, VertexFormats.POSITION);
		bb.vertex(mat, -1.5f, 0, -1.5f).next();
		bb.vertex(mat,  1.5f, 0, -1.5f).next();
		bb.vertex(mat,  1.5f, 0,  1.5f).next();
		bb.vertex(mat, -1.5f, 0,  1.5f).next();
		tess.draw();
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.enableTexture();
		GlStateManager.enableCull();
	}
	
	public static float sCurve5(float a) {
		float a3 = a * a * a;
		float a4 = a3 * a;
		float a5 = a4 * a;
		return (6 * a5) - (15 * a4) + (10 * a3);
	}
	
	private void drawVoidFace(World w, MatrixStack ms, VertexConsumer vc, BlockPos pos, Direction face) {
		EffectorWorld ew = (EffectorWorld)w;
		if (ew.yttr$isPhased(pos)) return;
		if (w.isAir(pos.offset(face))) return;
		BakedModel model = mc.getBlockRenderManager().getModel(mc.world.getBlockState(pos));
		if (model == null) return;
		Iterable<BakedQuad> quads = model.getQuads(null, face, mc.world.random);
		if (quads == null) return;
		ms.push();
		ms.translate(pos.getX(), pos.getY(), pos.getZ());
		for (BakedQuad q : quads) {
			vc.quad(ms.peek(), q, 0, 0, 0, LightmapTextureManager.pack(0, 0), OverlayTexture.DEFAULT_UV);
		}
		ms.pop();
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
	}

	private <T> void eachRegisterableField(Class<?> holder, Class<T> type, BiConsumer<Field, T> cb) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					cb.accept(f, (T)f.get(null));
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
	
	public void renderRifle(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
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
	
	private final LampBlockEntityRenderer lampItemGlow = new LampBlockEntityRenderer(null);
	
	public void renderLamp(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BlockState state = ((BlockItem)stack.getItem()).getBlock().getDefaultState()
				.with(LampBlock.LIT, LampBlockItem.isInverted(stack))
				.with(LampBlock.COLOR, LampBlockItem.getColor(stack));
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
		matrices.translate(-0.5, -0.5, -0.5);
		BakedModel model = mc.getBlockRenderManager().getModel(state);
        int i = mc.getBlockColors().getColor(state, null, null, 0);
        float r = (i >> 16 & 255) / 255.0F;
        float g = (i >> 8 & 255) / 255.0F;
        float b = (i & 255) / 255.0F;
        mc.getBlockRenderManager().getModelRenderer().render(matrices.peek(),
        		vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), state, model, r, g, b, light, overlay);
        if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		lampItemGlow.render(mc.world, null, state, matrices, vertexConsumers, light, overlay);
	}
	
}
