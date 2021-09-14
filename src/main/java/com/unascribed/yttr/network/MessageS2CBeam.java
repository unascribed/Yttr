package com.unascribed.yttr.network;

import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class MessageS2CBeam extends S2CMessage {

	@MarshalledAs("i32")
	public int entityId;
	@MarshalledAs("i32")
	public int color;
	@MarshalledAs("f32")
	public float endX;
	@MarshalledAs("f32")
	public float endY;
	@MarshalledAs("f32")
	public float endZ;
	
	public MessageS2CBeam(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CBeam(int entityId, int color, float endX, float endY, float endZ) {
		super(YNetwork.CONTEXT);
		this.entityId = entityId;
		this.color = color;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		// NativeImage assumes little-endian, but our colors are big-endian, so swap red/blue
		float a = NativeImage.getAlpha(color)/255f;
		float r = NativeImage.getBlue(color)/255f;
		float g = NativeImage.getGreen(color)/255f;
		float b = NativeImage.getRed(color)/255f;
		Entity ent = player.world.getEntityById(entityId);
		if (ent == null) return;
		boolean fp = ent == player && mc.options.getPerspective() == Perspective.FIRST_PERSON;
		Vec3d start = RifleItem.getMuzzlePos(ent, fp);
		double len = MathHelper.sqrt(start.squaredDistanceTo(endX, endY, endZ));
		double diffX = endX-start.x;
		double diffY = endY-start.y;
		double diffZ = endZ-start.z;
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
	}

}
