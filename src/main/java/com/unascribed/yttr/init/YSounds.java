package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YSounds {

	public static final SoundEvent RIFLE_CHARGE = new SoundEvent(new Identifier("yttr", "rifle_charge"));
	public static final SoundEvent RIFLE_CHARGE_FAST = new SoundEvent(new Identifier("yttr", "rifle_charge_fast"));
	public static final SoundEvent RIFLE_CHARGE_CONTINUE = new SoundEvent(new Identifier("yttr", "rifle_charge_continue"));
	public static final SoundEvent RIFLE_CHARGE_RATTLE = new SoundEvent(new Identifier("yttr", "rifle_charge_rattle"));
	public static final SoundEvent RIFLE_CHARGE_CANCEL = new SoundEvent(new Identifier("yttr", "rifle_charge_cancel"));
	public static final SoundEvent RIFLE_FIRE = new SoundEvent(new Identifier("yttr", "rifle_fire"));
	public static final SoundEvent RIFLE_FIRE_DUD = new SoundEvent(new Identifier("yttr", "rifle_fire_dud"));
	public static final SoundEvent RIFLE_OVERCHARGE = new SoundEvent(new Identifier("yttr", "rifle_overcharge"));
	public static final SoundEvent RIFLE_VENT = new SoundEvent(new Identifier("yttr", "rifle_vent"));
	public static final SoundEvent RIFLE_LOAD = new SoundEvent(new Identifier("yttr", "rifle_load"));
	public static final SoundEvent RIFLE_WASTE = new SoundEvent(new Identifier("yttr", "rifle_waste"));
	public static final SoundEvent VOID_SOUND = new SoundEvent(new Identifier("yttr", "void"));
	public static final SoundEvent DISSOLVE = new SoundEvent(new Identifier("yttr", "dissolve"));
	public static final SoundEvent CRAFT_AWARE_HOPPER = new SoundEvent(new Identifier("yttr", "craft_aware_hopper"));
	public static final SoundEvent AWARE_HOPPER_AMBIENT = new SoundEvent(new Identifier("yttr", "aware_hopper_ambient"));
	public static final SoundEvent AWARE_HOPPER_BREAK = new SoundEvent(new Identifier("yttr", "aware_hopper_break"));
	public static final SoundEvent AWARE_HOPPER_SCREAM = new SoundEvent(new Identifier("yttr", "aware_hopper_scream"));
	public static final SoundEvent SWALLOW = new SoundEvent(new Identifier("yttr", "swallow"));

	public static void init() {
		Yttr.autoRegister(Registry.SOUND_EVENT, YSounds.class, SoundEvent.class);
	}
	
}
