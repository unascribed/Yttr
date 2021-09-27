package com.unascribed.yttr.content.block.note;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LowNoteBlock extends AltNoteBlock {

	public LowNoteBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void buildRemap(Builder<SoundEvent, SoundEvent> builder) {
		builder
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, YSounds.LOW_NOTE_BANJO)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, YSounds.LOW_NOTE_BASEDRUM)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASS, YSounds.LOW_NOTE_BASS)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BELL, YSounds.LOW_NOTE_BELL)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BIT, YSounds.LOW_NOTE_BIT)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, YSounds.LOW_NOTE_CHIME)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, YSounds.LOW_NOTE_COW_BELL)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, YSounds.LOW_NOTE_DIDGERIDOO)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, YSounds.LOW_NOTE_FLUTE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, YSounds.LOW_NOTE_GUITAR)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HARP, YSounds.LOW_NOTE_HARP)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HAT, YSounds.LOW_NOTE_HAT)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, YSounds.LOW_NOTE_IRON_XYLOPHONE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_PLING, YSounds.LOW_NOTE_PLING)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, YSounds.LOW_NOTE_SNARE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, YSounds.LOW_NOTE_XYLOPHONE);
	}

	@Override
	public int getOctaveOffset() {
		return -2;
	}

}
