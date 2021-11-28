package com.unascribed.yttr.mixin;

import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;

@Mixin(Util.class)
public class MixinUtil {

	@Inject(at=@At(value="FIELD", target="net/minecraft/util/Util.LOGGER:Lorg/apache/logging/log4j/Logger;"),
			method="getChoiceTypeInternal", cancellable=true)
	private static void getChoiceTypeInternal(TypeReference typeReference, String id, CallbackInfoReturnable<Type<?>> ci) {
		// shhh. SHHHH. SHUT UP. NO ONE CARES ABOUT DATA FIXERS.
		ci.setReturnValue(null);
	}
	
}
