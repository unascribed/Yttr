package com.unascribed.yttr;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.unascribed.yttr.util.QDCSS;
import com.unascribed.yttr.util.QDCSS.SyntaxErrorException;
import com.unascribed.yttr.util.YLog;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class YConfig {

	public enum Trilean {
		AUTO,
		ON,
		OFF,
		;
		public boolean resolve(boolean def) {
			if (this == AUTO) return def;
			return this == ON;
		}
	}
	
	private static final QDCSS defaults;
	private static final QDCSS data;
	static {
		URL url = YConfig.class.getResource("/yttr-default.css");
		try {
			defaults = QDCSS.load(url);
		} catch (IOException e) {
			throw new Error("Could not load config defaults", e);
		}
		File cfg = new File("config/yttr.css");
		if (!cfg.exists()) {
			try {
				Resources.asByteSource(url).copyTo(Files.asByteSink(cfg));
			} catch (IOException e) {
				YLog.error("IO error when copying default configuration", e);
			}
		}
		QDCSS dataTmp;
		try {
			dataTmp = QDCSS.load(cfg);
		} catch (IOException e) {
			YLog.error("IO error when reading configuration. Using defaults", e);
			dataTmp = defaults;
		} catch (SyntaxErrorException e) {
			YLog.error("Syntax error in configuration: {}. Using defaults", e.getMessage());
			dataTmp = defaults;
		}
		data = defaults.merge(dataTmp);
		
		General.touch();
		Client.touch();
		Rifle.touch();
		WorldGen.touch();
		Debug.touch();
	}
	
	public static final class General {
		public static final boolean trustPlayers    = data.getBoolean("general.trust-players").orElse(false);
		public static final boolean fixupDebugWorld = data.getBoolean("general.fixup-debug-world").orElse(true);
		
		private static void touch() {}
		private General() {}
	}
	
	public static final class Client {
		public static final boolean slopeSmoothing = data.getBoolean("client.slope-smoothing").orElse(true);
		
		private static void touch() {}
		private Client() {}
	}
	
	public static final class Rifle {
		public static final boolean allowVoid    = data.getBoolean("rifle.allow-void").orElse(true);
		public static final boolean allowExplode = data.getBoolean("rifle.allow-explode").orElse(true);
		public static final boolean allowFire    = data.getBoolean("rifle.allow-fire").orElse(true);
		
		private static void touch() {}
		private Rifle() {}
	}
	
	public static final class WorldGen {
		public static final boolean gadolinite = data.getBoolean("worldgen.gadolinite").orElse(true);
		public static final boolean brookite   = data.getBoolean("worldgen.brookite").orElse(true);
		public static final Trilean copper     = data.getEnum("worldgen.copper", Trilean.class).orElse(Trilean.AUTO);
		
		public static final boolean squeezeTrees = data.getBoolean("worldgen.squeeze-trees").orElse(true);
		public static final boolean wasteland    = data.getBoolean("worldgen.wasteland").orElse(true);
		
		public static final boolean coreLava = data.getBoolean("worldgen.core-lava").orElse(true);
		public static final boolean scorched  = data.getBoolean("worldgen.scorched").orElse(true);
		
		public static final boolean continuity = data.getBoolean("worldgen.continuity").orElse(true);
		
		private static void touch() {}
		private WorldGen() {}
	}
	
	public static final class Debug {
		public static final boolean registries            = data.getBoolean("debug.registries").orElse(false);
		public static final int     simulateLatency       = data.getInt("debug.simulate-latency").orElse(0);
		public static final int     simulateLatencyJitter = data.getInt("debug.simulate-latency-jitter").orElse(15);
		
		private static void touch() {}
		private Debug() {}
	}
	
}
