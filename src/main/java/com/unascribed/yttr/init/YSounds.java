package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YSounds {
	
	public static final SoundEvent RIFLE_CHARGE = create("rifle_charge");
	public static final SoundEvent RIFLE_CHARGE_FAST = create("rifle_charge_fast");
	public static final SoundEvent RIFLE_CHARGE_CONTINUE = create("rifle_charge_continue");
	public static final SoundEvent RIFLE_CHARGE_RATTLE = create("rifle_charge_rattle");
	public static final SoundEvent RIFLE_CHARGE_CANCEL = create("rifle_charge_cancel");
	public static final SoundEvent RIFLE_FIRE = create("rifle_fire");
	public static final SoundEvent RIFLE_FIRE_DUD = create("rifle_fire_dud");
	public static final SoundEvent RIFLE_OVERCHARGE = create("rifle_overcharge");
	public static final SoundEvent RIFLE_VENT = create("rifle_vent");
	public static final SoundEvent RIFLE_LOAD = create("rifle_load");
	public static final SoundEvent RIFLE_WASTE = create("rifle_waste");
	public static final SoundEvent VOID = create("void");
	public static final SoundEvent DISSOLVE = create("dissolve");
	public static final SoundEvent CRAFT_AWARE_HOPPER = create("craft_aware_hopper");
	public static final SoundEvent AWARE_HOPPER_AMBIENT = create("aware_hopper_ambient");
	public static final SoundEvent AWARE_HOPPER_BREAK = create("aware_hopper_break");
	public static final SoundEvent AWARE_HOPPER_SCREAM = create("aware_hopper_scream");
	public static final SoundEvent SWALLOW = create("swallow");
	public static final SoundEvent CLEAVER = create("cleaver");
	public static final SoundEvent SNARE_PLOP = create("snare_plop");
	public static final SoundEvent SNARE_GRAB = create("snare_grab");
	public static final SoundEvent SNARE_RELEASE = create("snare_release");
	public static final SoundEvent SNARE_BREAK = create("snare_break");
	public static final SoundEvent SNAP = create("snap");
	public static final SoundEvent CLANG = create("clang");
	public static final SoundEvent CHUTE_PLATED = create("chute_plated");
	public static final SoundEvent VOID_HOLE = create("void_hole");
	public static final SoundEvent EFFECTOR_OPEN = create("effector_open");
	public static final SoundEvent EFFECTOR_CLOSE = create("effector_close");
	public static final SoundEvent CENTRIFUGE = create("centrifuge");
	public static final SoundEvent CENTRIFUGE_CRACKLE = create("centrifuge_crackle");
	public static final SoundEvent HONK = create("honk");
	public static final SoundEvent DIVE = create("dive");
	public static final SoundEvent DANGER = create("danger");
	public static final SoundEvent EQUIP_SUIT = create("equip_suit");
	public static final SoundEvent DIVE_MONO = create("dive_mono");
	public static final SoundEvent DIVE_THRUST = create("dive_thrust");
	public static final SoundEvent DIVE_END = create("dive_end");
	public static final SoundEvent SUIT_STATION_CRACKLE = create("suit_station_crackle");
	public static final SoundEvent SUIT_STATION_WELD = create("suit_station_weld");
	public static final SoundEvent SUIT_STATION_MELT = create("suit_station_melt");
	public static final SoundEvent SUIT_STATION_USE_PLATE = create("suit_station_use_plate");
	public static final SoundEvent SKELETAL_SORTER_AMBIENT = create("skeletal_sorter_ambient");
	public static final SoundEvent SKELETAL_SORTER_HURT = create("skeletal_sorter_hurt");
	public static final SoundEvent REPLICATOR_APPEAR = create("replicator_appear");
	public static final SoundEvent REPLICATOR_DISAPPEAR = create("replicator_disappear");
	public static final SoundEvent REPLICATOR_UPDATE = create("replicator_update");
	public static final SoundEvent REPLICATOR_REFUSE = create("replicator_refuse");
	public static final SoundEvent REPLICATOR_VEND = create("replicator_vend");
	public static final SoundEvent SILENCE = create("silence");
	

	public static void init() {
		Yttr.autoRegister(Registry.SOUND_EVENT, YSounds.class, SoundEvent.class);
	}

	private static SoundEvent create(String path) {
		return new SoundEvent(new Identifier("yttr", path));
	}
	
}
