package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;

@Mixin(BlockSoundGroup.class)
public interface AccessorBlockSoundGroup {

	@Accessor("breakSound")
	SoundEvent yttr$getBreakSound();
	
}
