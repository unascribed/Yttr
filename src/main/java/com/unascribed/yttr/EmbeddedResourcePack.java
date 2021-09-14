package com.unascribed.yttr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import com.unascribed.yttr.util.YLog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public class EmbeddedResourcePack implements ResourcePack {

	private final String path;
	
	private final JsonObject meta;
	
	public EmbeddedResourcePack(String path) {
		this.path = path;
		JsonObject meta;
		try {
			meta = new Gson().fromJson(Resources.toString(url("pack.mcmeta"), Charsets.UTF_8), JsonObject.class);
		} catch (Throwable t) {
			YLog.warn("Failed to load meta for internal resource pack "+path);
			meta = new JsonObject();
		}
		this.meta = meta;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public InputStream openRoot(String fileName) throws IOException {
		if (fileName.contains("/") || fileName.contains("\\")) {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
		InputStream is = getClass().getClassLoader().getResourceAsStream("packs/"+this.path+"/"+fileName);
		if (is == null) throw new FileNotFoundException(fileName);
		return is;
	}

	private URL url(ResourceType type, Identifier id) {
		return url(type.getDirectory()+"/"+id.getNamespace()+"/"+id.getPath());
	}
	
	private URL url(String path) {
		return getClass().getClassLoader().getResource("packs/"+this.path+"/"+path);
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		URL u = url(type, id);
		if (u == null) throw new FileNotFoundException(id.toString());
		return u.openStream();
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		// SORRY NO BONUS
		return Collections.emptySet();
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return url(type, id) != null;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return ImmutableSet.of("yttr");
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		if (!meta.has(metaReader.getKey())) return null;
		return metaReader.fromJson(meta.getAsJsonObject(metaReader.getKey()));
	}

	@Override
	public String getName() {
		return meta.getAsJsonObject("pack").get("name").getAsString();
	}

	@Override
	public void close() {
	}

}
