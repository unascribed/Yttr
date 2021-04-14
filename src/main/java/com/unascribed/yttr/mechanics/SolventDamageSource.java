package com.unascribed.yttr.mechanics;

import net.minecraft.entity.damage.DamageSource;

public class SolventDamageSource extends DamageSource {

	public final int i;
	
	public SolventDamageSource(int i) {
		super("yttr.solvent");
		this.i = i;
	}

}
