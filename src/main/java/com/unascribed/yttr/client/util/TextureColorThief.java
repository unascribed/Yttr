package com.unascribed.yttr.client.util;

import java.io.IOException;

import com.unascribed.yttr.repackage.de.androidpit.colorthief.ColorThiefMC;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture.TextureData;
import net.minecraft.util.Identifier;

public class TextureColorThief {

	public static final Identifier MISSINGNO = new Identifier("missingno");
	
	private static final Object2IntMap<Identifier> priCache = new Object2IntOpenHashMap<>();
	private static final Object2IntMap<Identifier> secCache = new Object2IntOpenHashMap<>();
	
	public static int getPrimaryColor(Identifier id) {
		if (!priCache.containsKey(id)) compute(id);
		return priCache.getInt(id);
	}
	
	public static int getSecondaryColor(Identifier id) {
		if (!secCache.containsKey(id)) compute(id);
		return secCache.getInt(id);
	}
	
	public static void clearCache() {
		priCache.clear();
		secCache.clear();
	}

	private static void compute(Identifier id) {
		int pri = 0xFF00FF;
		int sec = 0x000000;
		TextureData data = TextureData.load(MinecraftClient.getInstance().getResourceManager(), id);
		if (!MISSINGNO.equals(id)) {
			try {
				NativeImage ni = data.getImage();
				int[][] colors = ColorThiefMC.getPalette(ni, 5, 5, false);
				pri = pack(colors[0]);
				sec = pack(colors[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		priCache.put(id, pri);
		secCache.put(id, sec);
	}

	private static int pack(int[] arr) {
		return arr[0] | arr[1]<<8 | arr[2]<<16;
	}
	
	
}
