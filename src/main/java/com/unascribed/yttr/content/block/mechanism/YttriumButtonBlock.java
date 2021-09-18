package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YSounds;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;

public class YttriumButtonBlock extends AbstractButtonBlock {

	public YttriumButtonBlock(Settings settings) {
		super(false, settings);
	}

	@Override
	protected SoundEvent getClickSound(boolean powered) {
		return YSounds.METAL_BUTTON;
	}

}
