package com.unascribed.yttr.client.suit;

import com.unascribed.yttr.client.screen.SuitScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SuitMusic extends SuitSound implements TickableSoundInstance {

	public SuitMusic(SoundEvent event, float volume, SoundCategory category) {
		super(event, volume, category);
		this.looping = true;
	}

	@Override
	public boolean isDone() {
		return !(MinecraftClient.getInstance().currentScreen instanceof SuitScreen);
	}

	@Override
	public void tick() {
		// no-op, we just implement tickable so our volume gets updated dynamically
	}
	
	

}
