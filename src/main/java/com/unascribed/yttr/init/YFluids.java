package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.fluid.PureVoidFluid;
import com.unascribed.yttr.fluid.VoidFluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public class YFluids {

	public static final VoidFluid.Flowing FLOWING_VOID = new VoidFluid.Flowing();
	public static final VoidFluid.Still VOID = new VoidFluid.Still();
	
	public static final PureVoidFluid.Flowing FLOWING_PURE_VOID = new PureVoidFluid.Flowing();
	public static final PureVoidFluid.Still PURE_VOID = new PureVoidFluid.Still();

	public static void init() {
		Yttr.autoRegister(Registry.FLUID, YFluids.class, Fluid.class);
	}

}
