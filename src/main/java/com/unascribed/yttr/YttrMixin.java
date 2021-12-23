package com.unascribed.yttr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.unascribed.yttr.util.YLog;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class YttrMixin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
		
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		
	}

	@Override
	public List<String> getMixins() {
		return discoverClassesInPackage("com.unascribed.yttr.mixin", true);
	}

	public static List<String> discoverClassesInPackage(String pkg, boolean truncate) {
		List<String> rtrn = Lists.newArrayList();
		int skipped = 0;
		outer: for (ClassInfo ci : getClassesInPackage(pkg)) {
			// we want nothing to do with inner classes and the like
			if (ci.getName().contains("$")) continue;
			try {
				ClassReader cr = new ClassReader(ci.asByteSource().read());
				ClassNode cn = new ClassNode();
				cr.accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
				if (cn.invisibleAnnotations != null) {
					for (AnnotationNode an : cn.invisibleAnnotations) {
						if (an.desc.equals("Lnet/fabricmc/api/Environment;")) {
							if (an.values == null) continue;
							for (int i = 0; i < an.values.size(); i += 2) {
								String k = (String)an.values.get(i);
								Object v = an.values.get(i+1);
								if ("value".equals(k) && v instanceof String[]) {
									String[] arr = (String[])v;
									if (arr[0].equals("Lnet/fabricmc/api/EnvType;")) {
										EnvType e = EnvType.valueOf(arr[1]);
										if (e != FabricLoader.getInstance().getEnvironmentType()) {
											YLog.debug("Skipping {} mixin {}", e, ci.getName());
											skipped++;
											continue outer;
										}
									}
								}
							}
						}
					}
				}
				String truncName = ci.getName().substring(pkg.length()+1);
				rtrn.add(truncate ? truncName : ci.getName());
			} catch (IOException e) {
				YLog.warn("Exception while trying to read {}", ci.getName(), e);
			}
		}
		YLog.info("Discovered {} classes in {} (skipped {})", rtrn.size(), pkg, skipped);
		return rtrn;
	}

	private static Iterable<ClassInfo> getClassesInPackage(String pkg) {
		try (InputStream is = YttrMixin.class.getClassLoader().getResourceAsStream("yttr-classes.txt")) {
			if (is != null) {
				List<ClassInfo> rtrn = Lists.newArrayList();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
				String prefix = pkg.replace('.', '/')+"/";
				while (true) {
					String line = br.readLine();
					if (line == null) break;
					if (line.startsWith(prefix)) {
						rtrn.add(new BareClassInfo(line, YttrMixin.class.getClassLoader()));
					}
				}
				return rtrn;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			YLog.warn("Attempting to dynamically discover classes; this will only work in a development environment!");
			return Iterables.transform(ClassPath.from(YttrMixin.class.getClassLoader()).getTopLevelClassesRecursive(pkg), GuavaClassInfo::new);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}
	
	private interface ClassInfo {

		String getName();
		ByteSource asByteSource();
		
	}
	
	private static class BareClassInfo implements ClassInfo {

		private final String name;
		private final ClassLoader loader;
		
		public BareClassInfo(String name, ClassLoader loader) {
			this.name = name;
			this.loader = loader;
		}

		@Override
		public String getName() {
			return name.replace('/', '.').replace(".class", "");
		}

		@Override
		public ByteSource asByteSource() {
			return Resources.asByteSource(loader.getResource(name));
		}
		
	}
	
	private static class GuavaClassInfo implements ClassInfo {
		
		private final ClassPath.ClassInfo delegate;

		public GuavaClassInfo(ClassPath.ClassInfo delegate) {
			this.delegate = delegate;
		}

		@Override
		public String getName() {
			return delegate.getName();
		}

		@Override
		public ByteSource asByteSource() {
			return delegate.asByteSource();
		}

	}

}
