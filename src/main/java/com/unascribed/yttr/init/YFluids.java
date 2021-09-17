package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.fluid.CoreLavaFluid;
import com.unascribed.yttr.content.fluid.PureVoidFluid;
import com.unascribed.yttr.content.fluid.VoidFluid;
import com.unascribed.yttr.util.annotate.ConstantColor;
import com.unascribed.yttr.util.annotate.RenderLayer;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public class YFluids {

	@ConstantColor(0xAAAAAA)
	@Sprite("yttr:block/void_flow")
	public static final VoidFluid.Flowing FLOWING_VOID = new VoidFluid.Flowing();
	@ConstantColor(0xAAAAAA)
	@Sprite("yttr:block/void_still")
	public static final VoidFluid.Still VOID = new VoidFluid.Still();
	
	@RenderLayer("translucent")
	@Sprite("yttr:block/pure_void_flow")
	public static final PureVoidFluid.Flowing FLOWING_PURE_VOID = new PureVoidFluid.Flowing();
	@RenderLayer("translucent")
	@Sprite("yttr:block/pure_void_still")
	public static final PureVoidFluid.Still PURE_VOID = new PureVoidFluid.Still();
	
	@Sprite("minecraft:block/lava_still")
	@ConstantColor(0xFFAAAA)
	public static final CoreLavaFluid CORE_LAVA = new CoreLavaFluid();

	public static void init() {
		Yttr.autoRegister(Registry.FLUID, YFluids.class, Fluid.class);
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Sprite {
		String value();
	}

}
