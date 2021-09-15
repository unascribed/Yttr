package com.unascribed.yttr.client.suit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SuitSound extends PositionedSoundInstance {

	public SuitSound(SoundEvent event) {
		this(event, 5);
	}
	
	public SuitSound(SoundEvent event, float volume) {
		this(event, volume, SoundCategory.MASTER);
	}
	
	public SuitSound(SoundEvent event, float volume, SoundCategory category) {
		super(event, category, volume, 1, 0, 0, 0);
		attenuationType = AttenuationType.NONE;
	}
	
	@Override
	public boolean canPlay() {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null) {
			x = mc.player.getPos().x;
			y = mc.player.getPos().y;
			z = mc.player.getPos().z;
		}
		return true;
	}
	
	public void setVolume(float v) {
		this.volume = v;
	}
	
}
