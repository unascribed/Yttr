package com.unascribed.yttr.mixin.note;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.content.block.note.AltNoteBlock;

import net.minecraft.block.NoteBlock;
import net.minecraft.sound.SoundEvent;

@Mixin(value=NoteBlock.class, priority=1100)
public class MixinFabricationNoteBlock {
	
	@ModifyVariable(at=@At(value="INVOKE", target="com/google/common/math/IntMath.mod(II)I"),
			method="fabrication$informNote", ordinal=1, require=0, remap=false)
	private int modifyBaseOctave(int base) {
		if (base == -1) return base;
		Object self = this;
		if (self instanceof AltNoteBlock) {
			return base+((AltNoteBlock)self).getOctaveOffset();
		}
		return base;
	}
	
	@ModifyArg(at=@At(value="INVOKE", target="net/minecraft/world/World.playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"),
			method="playNote", require=0)
	private SoundEvent changeSoundEvent(SoundEvent se) {
		Object self = this;
		if (self instanceof AltNoteBlock) {
			return ((AltNoteBlock)self).remap(se);
		}
		return se;
	}
	
	
}
