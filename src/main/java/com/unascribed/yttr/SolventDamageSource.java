package com.unascribed.yttr;

import net.minecraft.entity.damage.DamageSource;

public class SolventDamageSource extends DamageSource {

	public final int i;
	
	protected SolventDamageSource(int i) {
		super("yttr.solvent");
		this.i = i;
	}

}
