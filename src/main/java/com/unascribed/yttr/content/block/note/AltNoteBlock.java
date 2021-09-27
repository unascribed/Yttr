package com.unascribed.yttr.content.block.note;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.block.NoteBlock;
import net.minecraft.sound.SoundEvent;

public abstract class AltNoteBlock extends NoteBlock {

	protected final ImmutableMap<SoundEvent, SoundEvent> remap;
	
	public AltNoteBlock(Settings settings) {
		super(settings);
		Builder<SoundEvent, SoundEvent> bldr = ImmutableMap.builder();
		buildRemap(bldr);
		this.remap = bldr.build();
	}

	public SoundEvent remap(SoundEvent event) {
		return remap.getOrDefault(event, event);
	}
	
	public abstract void buildRemap(Builder<SoundEvent, SoundEvent> builder);
	public abstract int getOctaveOffset();
	
}
