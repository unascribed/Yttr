package com.unascribed.yttr.content.block.note;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class HighNoteBlock extends AltNoteBlock {

	public HighNoteBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void buildRemap(Builder<SoundEvent, SoundEvent> builder) {
		builder
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, YSounds.HIGH_NOTE_BANJO)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, YSounds.HIGH_NOTE_BASEDRUM)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASS, YSounds.HIGH_NOTE_BASS)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BELL, YSounds.HIGH_NOTE_BELL)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BIT, YSounds.HIGH_NOTE_BIT)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, YSounds.HIGH_NOTE_CHIME)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, YSounds.HIGH_NOTE_COW_BELL)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, YSounds.HIGH_NOTE_DIDGERIDOO)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, YSounds.HIGH_NOTE_FLUTE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, YSounds.HIGH_NOTE_GUITAR)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HARP, YSounds.HIGH_NOTE_HARP)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HAT, YSounds.HIGH_NOTE_HAT)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, YSounds.HIGH_NOTE_IRON_XYLOPHONE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_PLING, YSounds.HIGH_NOTE_PLING)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, YSounds.HIGH_NOTE_SNARE)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, YSounds.HIGH_NOTE_XYLOPHONE);
	}

	@Override
	public int getOctaveOffset() {
		return 2;
	}

}
