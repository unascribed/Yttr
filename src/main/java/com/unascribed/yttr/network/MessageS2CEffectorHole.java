package com.unascribed.yttr.network;

import com.unascribed.yttr.client.render.EffectorRenderer;
import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.network.concrete.NetworkContext;
import com.unascribed.yttr.network.concrete.S2CMessage;
import com.unascribed.yttr.network.concrete.annotation.field.MarshalledAs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MessageS2CEffectorHole extends S2CMessage {
	
	public BlockPos pos;
	public Direction dir;
	@MarshalledAs("u8")
	public int dist;
	
	public MessageS2CEffectorHole(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CEffectorHole(BlockPos pos, Direction dir, int dist) {
		super(YNetwork.CONTEXT);
		this.pos = pos;
		this.dir = dir;
		this.dist = dist;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		BlockPos endPos = pos.offset(dir, dist);
		mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, pos));
		mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, pos), 130);
		for (int i = 0; i < dist; i += 4) {
			BlockPos midPos = pos.offset(dir, i);
			mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, midPos));
			mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, midPos), 130);
		}
		mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_OPEN, SoundCategory.BLOCKS, 0.4f, 1, endPos));
		mc.getSoundManager().play(new PositionedSoundInstance(YSounds.EFFECTOR_CLOSE, SoundCategory.BLOCKS, 0.4f, 1, endPos), 130);
		EffectorRenderer.addHole(pos, dir, dist);
		EffectorItem.effect(mc.world, pos, dir, null, null, dist, false);
	}

}
