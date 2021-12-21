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
	public static final SoundEvent RIFLE_SCOPE = create("rifle_scope");
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
	public static final SoundEvent SPECTRAL_AXE_DISAPPEAR = create("spectral_axe_disappear");
	public static final SoundEvent DSU_OPEN = create("dsu_open");
	public static final SoundEvent DSU_CLOSE = create("dsu_close");
	public static final SoundEvent DROP_CAST = create("drop_cast");
	public static final SoundEvent DROP_CAST_CANCEL = create("drop_cast_cancel");
	public static final SoundEvent DROP_CAST_CANCEL_AUDIBLE = create("drop_cast_cancel_audible");
	public static final SoundEvent ROOTBREAK = create("rootbreak");
	public static final SoundEvent ROOTSTEP = create("rootstep");
	public static final SoundEvent ROOTHIT = create("roothit");
	public static final SoundEvent ROOTHITWEAK = create("roothitweak");
	public static final SoundEvent ROOTPLACE = create("rootplace");
	public static final SoundEvent HOLLOWBREAKHUGE = create("hollowbreakhuge");
	public static final SoundEvent HOLLOWBREAK = create("hollowbreak");
	public static final SoundEvent HOLLOWSTEP = create("hollowstep");
	public static final SoundEvent HOLLOWHIT = create("hollowhit");
	public static final SoundEvent HOLLOWPLACEHUGE = create("hollowplacehuge");
	public static final SoundEvent HOLLOWPLACE = create("hollowplace");
	public static final SoundEvent METAL_BUTTON = create("metal_button");
	public static final SoundEvent PAPILLONS = create("papillons");
	public static final SoundEvent VOID_MUSIC = create("void_music");
	public static final SoundEvent DESERT_HEAT = create("desert_heat");
	public static final SoundEvent MEMORANDUM = create("memorandum");
	public static final SoundEvent VORPALHIT1 = create("vorpalhit1");
	public static final SoundEvent VORPALHIT2 = create("vorpalhit2");
	public static final SoundEvent BUZZ = create("buzz");
	public static final SoundEvent METAL_PLATE_ON = create("metal_plate_on");
	public static final SoundEvent METAL_PLATE_OFF = create("metal_plate_off");
	public static final SoundEvent SMALL_ZAP = create("small_zap");
	public static final SoundEvent PROJECT = create("project");
	public static final SoundEvent SHIFT = create("shift");
	public static final SoundEvent MAGNET_STEP = create("magnet_step");
	
	public static final SoundEvent HIGH_NOTE_BANJO = create("high_note.banjo");
	public static final SoundEvent HIGH_NOTE_BASEDRUM = create("high_note.basedrum");
	public static final SoundEvent HIGH_NOTE_BASS = create("high_note.bass");
	public static final SoundEvent HIGH_NOTE_BELL = create("high_note.bell");
	public static final SoundEvent HIGH_NOTE_BIT = create("high_note.bit");
	public static final SoundEvent HIGH_NOTE_CHIME = create("high_note.chime");
	public static final SoundEvent HIGH_NOTE_COW_BELL = create("high_note.cow_bell");
	public static final SoundEvent HIGH_NOTE_DIDGERIDOO = create("high_note.didgeridoo");
	public static final SoundEvent HIGH_NOTE_FLUTE = create("high_note.flute");
	public static final SoundEvent HIGH_NOTE_GUITAR = create("high_note.guitar");
	public static final SoundEvent HIGH_NOTE_HARP = create("high_note.harp");
	public static final SoundEvent HIGH_NOTE_HAT = create("high_note.hat");
	public static final SoundEvent HIGH_NOTE_IRON_XYLOPHONE = create("high_note.iron_xylophone");
	public static final SoundEvent HIGH_NOTE_PLING = create("high_note.pling");
	public static final SoundEvent HIGH_NOTE_SNARE = create("high_note.snare");
	public static final SoundEvent HIGH_NOTE_XYLOPHONE = create("high_note.xylophone");
	
	public static final SoundEvent LOW_NOTE_BANJO = create("low_note.banjo");
	public static final SoundEvent LOW_NOTE_BASEDRUM = create("low_note.basedrum");
	public static final SoundEvent LOW_NOTE_BASS = create("low_note.bass");
	public static final SoundEvent LOW_NOTE_BELL = create("low_note.bell");
	public static final SoundEvent LOW_NOTE_BIT = create("low_note.bit");
	public static final SoundEvent LOW_NOTE_CHIME = create("low_note.chime");
	public static final SoundEvent LOW_NOTE_COW_BELL = create("low_note.cow_bell");
	public static final SoundEvent LOW_NOTE_DIDGERIDOO = create("low_note.didgeridoo");
	public static final SoundEvent LOW_NOTE_FLUTE = create("low_note.flute");
	public static final SoundEvent LOW_NOTE_GUITAR = create("low_note.guitar");
	public static final SoundEvent LOW_NOTE_HARP = create("low_note.harp");
	public static final SoundEvent LOW_NOTE_HAT = create("low_note.hat");
	public static final SoundEvent LOW_NOTE_IRON_XYLOPHONE = create("low_note.iron_xylophone");
	public static final SoundEvent LOW_NOTE_PLING = create("low_note.pling");
	public static final SoundEvent LOW_NOTE_SNARE = create("low_note.snare");
	public static final SoundEvent LOW_NOTE_XYLOPHONE = create("low_note.xylophone");
	
	

	public static void init() {
		Yttr.autoRegister(Registry.SOUND_EVENT, YSounds.class, SoundEvent.class);
	}

	private static SoundEvent create(String path) {
		return new SoundEvent(new Identifier("yttr", path));
	}
	
}
