package com.unascribed.yttr.client.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.unascribed.yttr.util.YLog;

import com.google.gson.internal.UnsafeAllocator;
import com.unascribed.yttr.client.util.DummyServerWorld;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityRendererDispatcher;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SnareEntityTextureCache {

	private static final Supplier<World> dummyWorld;
	static {
		Supplier<World> dummyWorldTemp;
		try {
			DummyServerWorld sw = UnsafeAllocator.create().newInstance(DummyServerWorld.class);
			sw.init();
			dummyWorldTemp = () -> sw;
		} catch (Exception e) {
			dummyWorldTemp = () -> MinecraftClient.getInstance().world;
			YLog.warn("Failed to construct dummy ServerWorld, using client world directly. Snare color determination may be wrong for some entities!", e);
		}
		dummyWorld = dummyWorldTemp;
	}
	private static final Cache<NbtCompound, Identifier> textureCache = CacheBuilder.newBuilder()
			.expireAfterAccess(5, TimeUnit.SECONDS)
			.build();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Identifier get(ItemStack stack) {
		EntityType<?> type = YItems.SNARE.getEntityType(stack);
		if (type == null) return null;
		NbtCompound data = stack.getTag().getCompound("Contents");
		if (!textureCache.asMap().containsKey(data)) {
			if (type == EntityType.FALLING_BLOCK) {
				BlockState bs = NbtHelper.toBlockState(data.getCompound("BlockState"));
				BakedModel bm = MinecraftClient.getInstance().getBlockRenderManager().getModel(bs);
				Identifier id = bm.getSprite().getId();
				textureCache.put(data, new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
			} else if (type == EntityType.ITEM) {
				ItemStack item = ItemStack.fromNbt(data.getCompound("Item"));
				BakedModel bm = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(item);
				Identifier id = bm.getSprite().getId();
				textureCache.put(data, new Identifier(id.getNamespace(), "textures/"+id.getPath()+".png"));
			} else {
				EntityRenderer renderer = ((AccessorEntityRendererDispatcher)MinecraftClient.getInstance().getEntityRenderDispatcher()).yttr$getRenderers().get(type);
				if (renderer == null) {
					textureCache.put(data, TextureColorThief.MISSINGNO);
				} else {
					try {
						textureCache.put(data, renderer.getTexture(YItems.SNARE.createEntity(dummyWorld.get(), stack)));
					} catch (Throwable e) {
						YLog.debug("Failed to determine color for entity", e);
						textureCache.put(data, TextureColorThief.MISSINGNO);
					}
				}
			}
		}
		return textureCache.getIfPresent(data);
	}
	
}
